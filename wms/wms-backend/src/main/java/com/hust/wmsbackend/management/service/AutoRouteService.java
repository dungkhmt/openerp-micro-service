package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.model.DeliveryTripDTO;
import com.hust.wmsbackend.management.model.response.AutoRouteResponse;

import java.security.Principal;

public interface AutoRouteService {

    void route(Principal principal, DeliveryTripDTO request);

    AutoRouteResponse getPath(String deliveryTripId);
}
