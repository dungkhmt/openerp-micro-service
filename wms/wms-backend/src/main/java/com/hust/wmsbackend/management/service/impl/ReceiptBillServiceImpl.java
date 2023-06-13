package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.ReceiptBill;
import com.hust.wmsbackend.management.model.response.ReceiptBillWithItems;
import com.hust.wmsbackend.management.repository.ReceiptBillRepository;
import com.hust.wmsbackend.management.repository.ReceiptItemRepository;
import com.hust.wmsbackend.management.service.ReceiptBillService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor
@Slf4j
public class ReceiptBillServiceImpl implements ReceiptBillService {

    private ReceiptBillRepository receiptBillRepository;
    private ReceiptItemRepository receiptItemRepository;

    @Override
    public ReceiptBillWithItems getById(String id) {
        Optional<ReceiptBill> bill = receiptBillRepository.findById(id);
        if (!bill.isPresent()) {
            log.warn(String.format("Receipt bill with id %s is not exist", id));
            return null;
        }
        ReceiptBillWithItems response = new ReceiptBillWithItems(bill.get());
        response.setProcessedItems(receiptItemRepository.getProcessedItemsByReceiptBillId(bill.get().getReceiptBillId()));
        return response;
    }
}
