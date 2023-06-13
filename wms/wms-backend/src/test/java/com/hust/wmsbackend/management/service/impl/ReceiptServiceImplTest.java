package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.WmsBackendApplication;
import com.hust.wmsbackend.management.entity.ReceiptBill;
import com.hust.wmsbackend.management.repository.ReceiptBillRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = WmsBackendApplication.class)
class ReceiptServiceImplTest {

    @Autowired
    private ReceiptBillRepository receiptBillRepository;

    @Test
    public void insertReceiptBill() {
        ReceiptBill bill = ReceiptBill.builder().description("fake").receiptId(UUID.fromString("369c634a-821a-469d-842a-0841fd1c8619")).build();
        ReceiptBill bill2 = ReceiptBill.builder().description("fake").receiptId(UUID.fromString("31ae0e0c-e1eb-4915-8b45-93bdc472dde3")).build();
        receiptBillRepository.save(bill);
        receiptBillRepository.save(bill2);
    }
}