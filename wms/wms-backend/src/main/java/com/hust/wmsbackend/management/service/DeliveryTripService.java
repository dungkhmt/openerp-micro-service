package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.model.DeliveryTripDTO;

import java.security.Principal;
import java.util.List;

public interface DeliveryTripService {

    DeliveryTripDTO create(Principal principal, DeliveryTripDTO request);

    List<DeliveryTripDTO> getAll();

    DeliveryTripDTO getById(String tripId);

    DeliveryTripDTO deleteById(String tripId);

    DeliveryTripDTO estimateDistance(String deliveryTripId);

    List<DeliveryTripDTO> getTodayDeliveryTrip(Principal principal);

    boolean complete(String deliveryTripId);

    boolean startDelivery(String deliveryTripId, Principal principal);

    DeliveryTripDTO deleteFromShipmentById(String tripId);

}
