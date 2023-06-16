package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.model.response.ReceiptBillWithItems;

public interface ReceiptBillService {

    ReceiptBillWithItems getById(String id);

}
