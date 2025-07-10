package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.model.request.SaleOrderRequest;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

public interface SaleOrderService {
    boolean createSaleOrder(Principal principal, SaleOrderRequest request);

    void updateStatusByDeliveryTripItem(Set<UUID> orderIds);
}
