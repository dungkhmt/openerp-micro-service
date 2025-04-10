package com.hust.wmsbackend.management.service.service2;

import com.hust.wmsbackend.management.model.DeliveryTripDTO;
import com.hust.wmsbackend.management.model.response.AutoRouteResponse;

import java.security.Principal;

public interface AutoRouteService {

    void route(Principal principal, String token, DeliveryTripDTO request);

    AutoRouteResponse getPath(String deliveryTripId);

    AutoRouteResponse autoRoute2(DeliveryTripDTO request, Principal principal);
}
