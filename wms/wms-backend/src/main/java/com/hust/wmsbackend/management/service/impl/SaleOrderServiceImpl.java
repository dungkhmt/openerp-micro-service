package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.CustomerAddress;
import com.hust.wmsbackend.management.entity.SaleOrderHeader;
import com.hust.wmsbackend.management.entity.SaleOrderItem;
import com.hust.wmsbackend.management.entity.enumentity.OrderStatus;
import com.hust.wmsbackend.management.entity.enumentity.OrderType;
import com.hust.wmsbackend.management.entity.enumentity.PaymentType;
import com.hust.wmsbackend.management.model.request.CartItemRequest;
import com.hust.wmsbackend.management.model.request.SaleOrderRequest;
import com.hust.wmsbackend.management.model.response.CartItemResponse;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.service.CartService;
import com.hust.wmsbackend.management.service.ProductService;
import com.hust.wmsbackend.management.service.SaleOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SaleOrderServiceImpl implements SaleOrderService {

    private SaleOrderHeaderRepository saleOrderHeaderRepository;
    private SaleOrderItemRepository saleOrderItemRepository;
    private CustomerAddressRepository customerAddressRepository;
    private DeliveryTripItemRepository deliveryTripItemRepository;

    private CartService cartService;
    private ProductService productService;

    @Override
    @Transactional
    public boolean createSaleOrder(Principal principal, SaleOrderRequest request) {
        String userLoginId = principal.getName();
        CustomerAddress customerAddress;
        if (request.getCustomerAddressId() == null) {
            customerAddress = CustomerAddress.builder()
                .customerAddressId(UUID.randomUUID())
                .addressName(request.getAddressName())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .userLoginId(userLoginId)
                .build();
            customerAddressRepository.save(customerAddress);
        } else {
            Optional<CustomerAddress> customerAddressOpt = customerAddressRepository.findById(request.getCustomerAddressId());
            if (customerAddressOpt.isPresent()) {
                customerAddress = customerAddressOpt.get();
                customerAddressRepository.save(customerAddress);
            } else {
                log.warn(String.format("Customer address id %s is not found", request.getCustomerAddressId()));
                return false;
            }
        }

        OrderType orderType = OrderType.findByCode(request.getOrderTypeCode());
        if (orderType == null) {
            log.warn("Order type is null");
            return false;
        }

        PaymentType paymentType = PaymentType.findByCode(request.getPaymentTypeCode());
        if (paymentType == null) {
            log.warn("Payment type is null");
            return false;
        }

        CartItemResponse cartItemResponse = cartService.calculateCartFee(CartItemRequest
            .builder()
            .items(request.getItems())
            .longitude(request.getLongitude())
            .latitude(request.getLatitude())
            .build());
        SaleOrderHeader saleOrderHeader = SaleOrderHeader.builder()
            .orderId(UUID.randomUUID())
            .userLoginId(userLoginId)
            .orderDate(new Date())
            .deliveryFee(cartItemResponse.getDeliveryFee())
            .totalProductCost(cartItemResponse.getTotalProductCost())
            .totalOrderCost(cartItemResponse.getTotalOrderCost())
            .customerAddressId(customerAddress.getCustomerAddressId())
            .customerName(request.getCustomerName())
            .customerPhoneNumber(request.getCustomerPhoneNumber())
            .description(request.getDescription())
            .paymentType(paymentType)
            .orderType(orderType)
            .status(OrderStatus.CREATED)
            .build();
        UUID orderId = saleOrderHeader.getOrderId();
        List<SaleOrderItem> saleOrderItemList = request.getItems().stream().map(item -> SaleOrderItem
            .builder()
            .saleOrderItemId(UUID.randomUUID())
            .orderId(orderId)
            .productId(UUID.fromString(item.getProductId()))
            .quantity(item.getQuantity())
            .priceUnit(productService.getCurrPriceByProductId(UUID.fromString(item.getProductId())))
            .build()).collect(Collectors.toList());

        saleOrderHeaderRepository.save(saleOrderHeader);
        saleOrderItemRepository.saveAll(saleOrderItemList);
        return true;
    }

    @Override
    @Transactional
    public void updateStatusByDeliveryTripItem(Set<UUID> orderIds) {
        List<SaleOrderHeader> updateOrders = new ArrayList<>();
        for (UUID orderId : orderIds) {
            Optional<SaleOrderHeader> orderOpt = saleOrderHeaderRepository.findById(orderId);
            if (!orderOpt.isPresent()) {
                log.warn(String.format("Order id %s is not exist", orderId));
                continue;
            }
            SaleOrderHeader order = orderOpt.get();
            boolean isComplete = true;

            List<SaleOrderItem> items = saleOrderItemRepository.findAllByOrderId(orderId);
            for (SaleOrderItem item : items) {
                long orderQuantity = item.getQuantity();
                Long totalComplete = deliveryTripItemRepository.getTotalDoneDeliveryItemByOrderIdAndProductId(orderId, item.getProductId());
                if (totalComplete == null || orderQuantity != totalComplete) {
                    isComplete = false;
                    break;
                }
            }
            if (isComplete) {
                order.setStatus(OrderStatus.COMPLETED);
            } else {
                order.setStatus(OrderStatus.DELIVERING_A_PART);
            }
            updateOrders.add(order);
        }
        saleOrderHeaderRepository.saveAll(updateOrders);
    }
}
