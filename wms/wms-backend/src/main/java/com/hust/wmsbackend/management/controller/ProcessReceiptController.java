package com.hust.wmsbackend.management.controller;

import com.hust.wmsbackend.management.model.response.ProcessedItemModel;
import com.hust.wmsbackend.management.model.response.ReceiptProcessResponse;
import com.hust.wmsbackend.management.service.ReceiptService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wmsv2/process/receipt-request")
@Validated
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProcessReceiptController {

    private ReceiptService receiptService;

    @GetMapping(path = "/{receiptId}")
    public ResponseEntity<ReceiptProcessResponse> getReceiptForProcessing(@PathVariable String receiptId) {
        return ResponseEntity.ok(receiptService.getForProcessingById(receiptId));
    }

    @PostMapping(path = "/{receiptId}")
    public ResponseEntity<String> saveReceiptItemProcessed(@PathVariable String receiptId,
                                                           @RequestParam(name = "isDone") boolean isDone,
                                                           @RequestBody List<ProcessedItemModel> items) {
        return receiptService.process(receiptId, items, isDone) ?
            ResponseEntity.ok("OK") :
            new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
