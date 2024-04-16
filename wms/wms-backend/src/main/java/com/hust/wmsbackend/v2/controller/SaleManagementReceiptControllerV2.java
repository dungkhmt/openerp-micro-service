package com.hust.wmsbackend.v2.controller;

import com.hust.wmsbackend.management.model.ReceiptRequest;
import com.hust.wmsbackend.management.model.response.ReceiptRequestResponse;
import com.hust.wmsbackend.v2.service.ReceiptService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/wmsv3/sale-management/receipt")
@Validated
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SaleManagementReceiptControllerV2 {

    private ReceiptService receiptService;

    @GetMapping(path = "/approval-listing")
    public ResponseEntity<List<ReceiptRequestResponse>> getListReceiptsForSaleManager(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(receiptService.getAllForSaleManagement(status));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ReceiptRequestResponse> getReceiptDetailForSaleManagerById(@PathVariable String id) {
        return ResponseEntity.ok(receiptService.getForSaleManagementById(id));
    }

    @PutMapping(path = "/cancel/{id}")
    public ResponseEntity<String> cancelReceiptRequest(Principal principal, @PathVariable String id) {
        if (receiptService.cancel(principal, id)) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }

    @PutMapping(path = "/approve/{id}")
    public ResponseEntity<String> approveReceiptRequest(Principal principal, @PathVariable String id) {
        if (receiptService.approve(principal, id)) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }

    @GetMapping
    public ResponseEntity<List<ReceiptRequestResponse>> getListReceiptsForSaleManager(
            Principal principal,
            @RequestParam(name = "status", required = false) String[] statuses) {
        return ResponseEntity.ok(receiptService.getListReceiptsForSaleManager(principal, statuses));
    }

}
