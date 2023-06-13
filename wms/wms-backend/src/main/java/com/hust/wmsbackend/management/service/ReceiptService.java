package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.entity.Receipt;
import com.hust.wmsbackend.management.model.ReceiptRequest;
import com.hust.wmsbackend.management.model.response.*;

import java.security.Principal;
import java.util.List;

public interface ReceiptService {

    Receipt createReceipt(Principal principal, ReceiptRequest request);

    List<ReceiptGeneralResponse> getAllReceiptGeneral();

    ReceiptRequest getById(String id);

    List<ReceiptRequestResponse> getForSaleManagement(Principal principal, String[] statuses);

    List<ReceiptRequestResponse> getAllForSaleManagement(String status);

    ReceiptRequestResponse getForSaleManagementById(String id);

    boolean approve(Principal principal, String id);

    boolean cancel(Principal principal, String id);

    ReceiptProcessResponse getForProcessingById(String receiptId);

    boolean process(String receiptId, List<ProcessedItemModel> items, boolean isDone, Principal principal);

    List<ReceiptBillWithItems> getReceiptBills(String receiptId);
}
