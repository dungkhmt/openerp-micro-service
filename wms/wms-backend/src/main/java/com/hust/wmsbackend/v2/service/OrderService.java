package com.hust.wmsbackend.v2.service;

import com.hust.wmsbackend.management.model.response.OrderDetailResponse;
import com.hust.wmsbackend.management.model.response.OrderGeneralResponse;

import java.security.Principal;
import java.util.List;

public interface OrderService {

    List<OrderGeneralResponse> getAllOrdersByStatuses(String[] orderStatus);

    OrderDetailResponse getOrderDetailById(String orderId);

    boolean approve(Principal principal, String orderId);

    boolean cancel(Principal principal, String orderId);

    OrderDetailResponse getOrderDetailForWhManagerById(String orderIdStr);
}
