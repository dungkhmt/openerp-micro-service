package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.model.response.DeliveryBillWithItems;

import java.util.List;

public interface DeliveryBillService {

    List<DeliveryBillWithItems> getAll();

    DeliveryBillWithItems getByDeliveryBillId(String deliveryBillId);

}
