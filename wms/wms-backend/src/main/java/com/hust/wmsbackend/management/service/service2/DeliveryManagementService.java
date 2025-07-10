package com.hust.wmsbackend.management.service.service2;

import com.hust.wmsbackend.management.entity.DeliveryPerson;

import java.util.List;
import java.util.Map;

public interface DeliveryManagementService {

    List<DeliveryPerson> getAllDeliveryPersons();

    DeliveryPerson create(DeliveryPerson deliveryPerson);

    boolean delete(String[] deliveryPersonId);

    Map<String, String> getDeliveryPersonNameMap();

    String getNamebyId(String userLoginId);

}
