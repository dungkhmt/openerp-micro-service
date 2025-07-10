package com.hust.wmsbackend.management.service.service2;

import com.hust.wmsbackend.management.model.response.DeliveryBillWithItems;

import java.util.List;

public interface DeliveryBillService {

    List<DeliveryBillWithItems> getAll();

    DeliveryBillWithItems getDetailDeliveryBillById(String deliveryBillId);

}
