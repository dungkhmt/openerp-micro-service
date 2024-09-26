package com.hust.wmsbackend.management.controller;

import com.hust.wmsbackend.management.model.response.DeliveryBillWithItems;
import com.hust.wmsbackend.management.service.DeliveryBillService;
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
@RequestMapping("/wmsv2/admin/delivery-bill")
@Validated
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryBillController {

    private DeliveryBillService deliveryBillService;

    @GetMapping
    public ResponseEntity<List<DeliveryBillWithItems>> getAll() {
        return ResponseEntity.ok(deliveryBillService.getAll());
    }

    @GetMapping("/{deliveryBillId}")
    public ResponseEntity<DeliveryBillWithItems> getById(@PathVariable String deliveryBillId) {
        return ResponseEntity.ok(deliveryBillService.getByDeliveryBillId(deliveryBillId));
    }
}
