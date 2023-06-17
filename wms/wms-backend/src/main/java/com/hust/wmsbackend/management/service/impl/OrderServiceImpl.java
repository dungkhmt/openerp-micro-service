package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.entity.enumentity.DeliveryTripItemStatus;
import com.hust.wmsbackend.management.entity.enumentity.OrderStatus;
import com.hust.wmsbackend.management.model.response.OrderDetailResponse;
import com.hust.wmsbackend.management.model.response.OrderGeneralResponse;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.service.BayService;
import com.hust.wmsbackend.management.service.OrderService;
import com.hust.wmsbackend.management.service.ProductService;
import com.hust.wmsbackend.management.service.WarehouseService;
import com.hust.wmsbackend.management.utils.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class OrderServiceImpl implements OrderService {

    private SaleOrderHeaderRepository saleOrderHeaderRepository;
    private CustomerAddressRepository customerAddressRepository;
    private SaleOrderItemRepository saleOrderItemRepository;
    private AssignedOrderItemRepository assignedOrderItemRepository;
    private DeliveryTripItemRepository deliveryTripItemRepository;

    private ProductService productService;
    private WarehouseService warehouseService;
    private BayService bayService;

    @Override
    public List<OrderGeneralResponse> getAllOrdersByStatus(String[] orderStatusStr) {
        List<SaleOrderHeader> orders;
        if (orderStatusStr == null) {
            orders = saleOrderHeaderRepository.findAllByOrderByOrderDateDesc();
        } else {
            List<OrderStatus> orderStatuses = Arrays.stream(orderStatusStr).map(OrderStatus::findByCode)
                                                    .collect(Collectors.toList());
            orders = saleOrderHeaderRepository.findAllByStatusInOrderByOrderDateDesc(orderStatuses);
        }
        return orders
            .stream()
            .map(order -> OrderGeneralResponse
                .builder()
                .orderId(order.getOrderId())
                .createdOrderDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, order.getOrderDate()))
                .orderType(order.getOrderType().getName())
                .status(order.getStatus().getName())
                .totalOrderCost(order.getTotalOrderCost())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public OrderDetailResponse getOrderDetailById(String orderIdStr) {
        UUID orderId = UUID.fromString(orderIdStr);
        Optional<SaleOrderHeader> saleOrderHeaderOpt = saleOrderHeaderRepository.findById(orderId);
        if (!saleOrderHeaderOpt.isPresent()) {
            log.warn(String.format("Order id %s is not exist", orderIdStr));
            return null;
        }
        Map<UUID, String> productNameMap = productService.getProductNameMap();

        SaleOrderHeader saleOrderHeader = saleOrderHeaderOpt.get();
        List<DeliveryTripItem> deliveryTripItems = deliveryTripItemRepository.getDeliveryTripItemByUserLoginId(saleOrderHeader.getUserLoginId());
        List<OrderDetailResponse.OrderHistoryResponse> successProductHistory = new ArrayList<>();
        List<OrderDetailResponse.OrderHistoryResponse> failProductHistory = new ArrayList<>();
        BigDecimal totalSuccessOrderCost = BigDecimal.ZERO;
        BigDecimal totalFailOrderCost = BigDecimal.ZERO;

        for (DeliveryTripItem item : deliveryTripItems) {
            AssignedOrderItem assignedOrderItem = assignedOrderItemRepository.findById(item.getAssignedOrderItemId()).get();
            SaleOrderHeader order = saleOrderHeaderRepository.findById(assignedOrderItem.getOrderId()).get();
            SaleOrderItem orderItem = saleOrderItemRepository.findSaleOrderItemByOrderIdAndProductId(order.getOrderId(), assignedOrderItem.getProductId()).get();
            OrderDetailResponse.OrderHistoryResponse adder = OrderDetailResponse.OrderHistoryResponse.builder()
                    .productId(assignedOrderItem.getProductId().toString())
                    .productName(productNameMap.get(assignedOrderItem.getProductId()))
                    .orderId(item.getOrderId().toString())
                    .quantity(item.getQuantity())
                    .priceUnit(orderItem.getPriceUnit())
                    .address(customerAddressRepository.findById(order.getCustomerAddressId()).get().getAddressName())
                    .createdDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, order.getCreatedStamp()))
                    .build();
            if (item.getStatus() == DeliveryTripItemStatus.DONE) {
                successProductHistory.add(adder);
                totalSuccessOrderCost = totalSuccessOrderCost.add(orderItem.getPriceUnit());
            }
            if (item.getStatus() == DeliveryTripItemStatus.FAIL) {
                failProductHistory.add(adder);
                totalFailOrderCost = totalFailOrderCost.add(orderItem.getPriceUnit());
            }
        }

        Optional<CustomerAddress> customerAddressOpt = customerAddressRepository.findById(saleOrderHeader.getCustomerAddressId());
        if (!customerAddressOpt.isPresent()) {
            throw new RuntimeException(
                String.format("Customer address with id %s of order id %s not found",
                              saleOrderHeader.getCustomerAddressId(),
                              saleOrderHeader.getOrderId()));
        }

        List<SaleOrderItem> saleOrderItems = saleOrderItemRepository.findAllByOrderId(saleOrderHeader.getOrderId());
        List<OrderDetailResponse.OrderItemResponse> items = new ArrayList<>();
        for (SaleOrderItem item : saleOrderItems) {
            OrderDetailResponse.OrderItemResponse adder = OrderDetailResponse.OrderItemResponse.builder()
               .productId(item.getProductId())
               .productName(productNameMap.get(item.getProductId()))
               .quantity(BigDecimal.valueOf(item.getQuantity()))
               .priceUnit(item.getPriceUnit())
               .build();
            Long totalFail = deliveryTripItemRepository.getTotalFailDeliveryItemByOrderIdAndProductId(orderId, item.getProductId());
            if (totalFail == null || totalFail == 0) {
                adder.setDeliveryStatus("Giao hàng thành công");
            } else {
                adder.setDeliveryStatus("Giao hàng thất bại");
            }
            items.add(adder);
        }

        List<AssignedOrderItem> assignedItems = assignedOrderItemRepository.findAllByOrderId(orderId);
        Map<UUID, String> warehouseNameMap = warehouseService.getWarehouseNameMap();
        Map<UUID, String> bayCodeMap = bayService.getBayCodeMap();
        List<OrderDetailResponse.ProcessedOrderItemResponse> processedItems = assignedItems
            .stream()
            .map(item ->
                     OrderDetailResponse.ProcessedOrderItemResponse
                         .builder()
                         .productId(item.getProductId())
                         .productName(productNameMap.get(item.getProductId()))
                         .quantity(item.getOriginalQuantity())
                         .bayId(item.getBayId())
                         .bayCode(bayCodeMap.get(item.getBayId()))
                         .warehouseId(item.getWarehouseId())
                         .warehouseName(warehouseNameMap.get(item.getWarehouseId()))
                         .lotId(item.getLotId())
                         .status(item.getStatus().getName())
                         .createdDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, item.getCreatedStamp()))
                         .build())
            .collect(Collectors.toList());

        Map<UUID, BigDecimal> remainItemsMap = new HashMap<>();
        for (OrderDetailResponse.OrderItemResponse item : items) {
            remainItemsMap.put(item.getProductId(), item.getQuantity());
        }
        for (AssignedOrderItem item : assignedItems) {
            BigDecimal prevQuantity = remainItemsMap.get(item.getProductId());
            BigDecimal newQuantity = prevQuantity.subtract(item.getOriginalQuantity());
            if (newQuantity.compareTo(BigDecimal.ZERO) > 0){
                remainItemsMap.put(item.getProductId(), newQuantity);
            } else {
                remainItemsMap.remove(item.getProductId());
            }
        }
        List<OrderDetailResponse.OrderItemResponse> remainItems = new ArrayList<>();
        for (Map.Entry<UUID, BigDecimal> entry : remainItemsMap.entrySet()) {
            BigDecimal priceUnitLocal = null;
            for (OrderDetailResponse.OrderItemResponse item : items) {
                if (item.getProductId().equals(entry.getKey())) {
                    priceUnitLocal = item.getPriceUnit();
                    break;
                }
            }
            remainItems.add(OrderDetailResponse.OrderItemResponse.builder()
                                .productName(productNameMap.get(entry.getKey()))
                                .priceUnit(priceUnitLocal)
                                .quantity(entry.getValue())
                                .productId(entry.getKey()).build());
        }

        return OrderDetailResponse.builder()
            .userLoginId(saleOrderHeader.getUserLoginId())
            .customerName(saleOrderHeader.getCustomerName())
            .totalSuccessProductCost(totalSuccessOrderCost)
            .totalSuccessProductCount(BigDecimal.valueOf(successProductHistory.size()))
            .totalFailProductCost(totalFailOrderCost)
            .totalFailProductCount(BigDecimal.valueOf(failProductHistory.size()))
            .failProductHistory(failProductHistory)
            .successProductHistory(successProductHistory)
            .createdDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, saleOrderHeader.getCreatedStamp()))
            .paymentMethod(saleOrderHeader.getPaymentType().getName())
            .receiptAddress(customerAddressOpt.get().getAddressName())
            .totalOrderCost(saleOrderHeader.getTotalOrderCost())
            .status(saleOrderHeader.getStatus().getName())
            .statusCode(saleOrderHeader.getStatus().getCode())
            .processedItems(processedItems)
            .remainingItems(remainItems)
            .items(items)
            .cusAddLat(customerAddressOpt.get().getLatitude())
            .cusAddLon(customerAddressOpt.get().getLongitude())
            .build();
    }

    @Override
    public boolean approve(Principal principal,  String orderIdStr) {
        UUID orderId = UUID.fromString(orderIdStr);
        Optional<SaleOrderHeader> saleOrderHeaderOpt = saleOrderHeaderRepository.findById(orderId);
        if (!saleOrderHeaderOpt.isPresent()) {
            log.warn(String.format("Order id %s is not exist", orderIdStr));
            return false;
        }

        SaleOrderHeader saleOrderHeader = saleOrderHeaderOpt.get();
        if (saleOrderHeader.getStatus() != OrderStatus.CREATED) {
            log.warn(String.format("Sale order header is %s -> can not approve", saleOrderHeader.getStatus().getCode()));
            return false;
        }

        saleOrderHeader.setStatus(OrderStatus.APPROVED);
        saleOrderHeader.setApprovedBy(principal.getName());
        saleOrderHeaderRepository.save(saleOrderHeader);
        return true;
    }

    @Override
    public boolean cancel(Principal principal, String orderIdStr) {
        UUID orderId = UUID.fromString(orderIdStr);
        Optional<SaleOrderHeader> saleOrderHeaderOpt = saleOrderHeaderRepository.findById(orderId);
        if (!saleOrderHeaderOpt.isPresent()) {
            log.warn(String.format("Order id %s is not exist", orderIdStr));
            return false;
        }

        SaleOrderHeader saleOrderHeader = saleOrderHeaderOpt.get();
        saleOrderHeader.setStatus(OrderStatus.CANCELLED);
        saleOrderHeader.setCancelledBy(principal.getName());
        saleOrderHeaderRepository.save(saleOrderHeader);
        return true;
    }
}
