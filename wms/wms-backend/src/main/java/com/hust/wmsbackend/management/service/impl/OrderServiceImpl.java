package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.AssignedOrderItem;
import com.hust.wmsbackend.management.entity.CustomerAddress;
import com.hust.wmsbackend.management.entity.SaleOrderHeader;
import com.hust.wmsbackend.management.entity.SaleOrderItem;
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
            orders = saleOrderHeaderRepository.findAll();
        } else {
            List<OrderStatus> orderStatuses = Arrays.stream(orderStatusStr).map(OrderStatus::findByCode)
                                                    .collect(Collectors.toList());
            orders = saleOrderHeaderRepository.findAllByStatusIn(orderStatuses);
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

        SaleOrderHeader saleOrderHeader = saleOrderHeaderOpt.get();
        List<SaleOrderHeader> successCustomerOrders = saleOrderHeaderRepository
            .findAllByUserLoginIdAndStatus(saleOrderHeader.getUserLoginId(), OrderStatus.COMPLETED);
        List<SaleOrderHeader> cancelledCustomerOrders = saleOrderHeaderRepository
            .findAllByUserLoginIdAndStatus(saleOrderHeader.getUserLoginId(), OrderStatus.CUSTOMER_CANCELLED);
        BigDecimal totalSuccessOrderCost = successCustomerOrders.stream().map(SaleOrderHeader::getTotalOrderCost)
                                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCancelledOrderCost = cancelledCustomerOrders.stream().map(SaleOrderHeader::getTotalOrderCost)
                                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        Optional<CustomerAddress> customerAddressOpt = customerAddressRepository.findById(saleOrderHeader.getCustomerAddressId());
        if (!customerAddressOpt.isPresent()) {
            throw new RuntimeException(
                String.format("Customer address with id %s of order id %s not found",
                              saleOrderHeader.getCustomerAddressId(),
                              saleOrderHeader.getOrderId()));
        }

        Map<UUID, String> productNameMap = productService.getProductNameMap();
        List<SaleOrderItem> saleOrderItems = saleOrderItemRepository.findAllByOrderId(saleOrderHeader.getOrderId());
        List<OrderDetailResponse.OrderItemResponse> items = new ArrayList<>();
        for (SaleOrderItem item : saleOrderItems) {
            OrderDetailResponse.OrderItemResponse adder = OrderDetailResponse.OrderItemResponse.builder()
               .productId(item.getProductId())
               .productName(productNameMap.get(item.getProductId()))
               .quantity(BigDecimal.valueOf(item.getQuantity()))
               .priceUnit(item.getPriceUnit())
               .build();
            Long totalFail = deliveryTripItemRepository.getTotalDoneDeliveryItemByOrderIdAndProductId(orderId, item.getProductId());
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
                         .quantity(item.getQuantity())
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
            BigDecimal newQuantity = prevQuantity.subtract(item.getQuantity());
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
            .totalSuccessOrderCost(totalSuccessOrderCost)
            .totalSuccessOrderCount(BigDecimal.valueOf(successCustomerOrders.size()))
            .totalCancelledOrderCost(totalCancelledOrderCost)
            .totalCancelledOrderCount(BigDecimal.valueOf(cancelledCustomerOrders.size()))
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
