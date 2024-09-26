package com.hust.wmsbackend.management.service.service2;

import com.hust.wmsbackend.management.model.model2.request.SaleOrderRequest2;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

public interface SaleOrderService {
    boolean createSaleOrder(Principal principal, SaleOrderRequest2 request);

    void updateStatusByDeliveryTripItem(Set<UUID> orderIds);
}
