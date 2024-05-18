package com.hust.wmsbackend.v2.service;

import com.hust.wmsbackend.v2.model.request.SaleOrderRequest2;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

public interface SaleOrderService {
    boolean createSaleOrder(Principal principal, SaleOrderRequest2 request);

    void updateStatusByDeliveryTripItem(Set<UUID> orderIds);
}
