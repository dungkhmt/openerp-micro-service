package com.hust.wmsbackend.v2.service;

import com.hust.wmsbackend.management.model.response.ReceiptBillWithItems;

public interface ReceiptBillService {

    ReceiptBillWithItems getById(String id);

}
