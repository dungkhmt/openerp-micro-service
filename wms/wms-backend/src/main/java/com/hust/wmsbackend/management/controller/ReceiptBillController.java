package com.hust.wmsbackend.management.controller;


import com.hust.wmsbackend.management.model.response.ReceiptBillWithItems;
import com.hust.wmsbackend.management.service.ReceiptBillService;
import com.hust.wmsbackend.management.service.ReceiptService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wmsv2/admin/receipt-bill")
@Validated
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ReceiptBillController {

    private ReceiptService receiptService;
    private ReceiptBillService receiptBillService;

    @GetMapping
    public ResponseEntity<List<ReceiptBillWithItems>> getAllReceiptBills() {
        return ResponseEntity.ok(receiptService.getReceiptBills(null));
    }

    @GetMapping("/receipt-bill/{receiptBillId}")
    public ResponseEntity<ReceiptBillWithItems> getReceiptBillById(@PathVariable String receiptBillId) {
        return ResponseEntity.ok(receiptBillService.getById(receiptBillId));
    }

    @GetMapping("/receipt/{receiptId}")
    public ResponseEntity<List<ReceiptBillWithItems>> getReceiptBillsByReceiptId(@PathVariable(required = false) String receiptId) {
        return ResponseEntity.ok(receiptService.getReceiptBills(receiptId));
    }
}
