package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.entity.DeliveryPerson;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DeliveryManagementService {

    List<DeliveryPerson> getAllDeliveryPersons();

    DeliveryPerson create(DeliveryPerson deliveryPerson);

    boolean delete(String[] deliveryPersonId);

    Map<String, String> getDeliveryPersonNameMap();

}
