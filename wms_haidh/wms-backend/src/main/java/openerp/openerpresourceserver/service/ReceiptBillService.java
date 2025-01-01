package openerp.openerpresourceserver.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.entity.projection.ReceiptBillProjection;
import openerp.openerpresourceserver.repository.ReceiptBillRepository;

@Service
@RequiredArgsConstructor
public class ReceiptBillService {

    private final ReceiptBillRepository receiptBillRepository;

    public Page<ReceiptBillProjection> getAllReceiptBills(Pageable pageable) {
        return receiptBillRepository.findAllReceiptBills(pageable);
    }
}

