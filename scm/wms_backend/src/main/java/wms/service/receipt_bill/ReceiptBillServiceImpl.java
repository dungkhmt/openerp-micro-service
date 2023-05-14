package wms.service.receipt_bill;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import wms.dto.ReturnPaginationDTO;
import wms.entity.ReceiptBill;
import wms.entity.ReceiptBillItem;
import wms.repo.ReceiptBillItemRepo;
import wms.repo.ReceiptBillRepo;
import wms.service.BaseService;

import java.util.List;

@Service
@Slf4j
public class ReceiptBillServiceImpl extends BaseService implements IReceiptBillService {
    @Autowired
    private ReceiptBillRepo receiptBillRepo;
    @Autowired
    private ReceiptBillItemRepo receiptBillItemRepo;
    @Override
    public ReturnPaginationDTO<ReceiptBill> getAllBills(int page, int pageSize, String sortField, boolean isSortAsc, String orderCode) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<ReceiptBill> receiptBills = receiptBillRepo.getAllBills(pageable, orderCode);
        return getPaginationResult(receiptBills.getContent(), page, receiptBills.getTotalPages(), receiptBills.getTotalElements());
    }

    @Override
    public List<ReceiptBillItem> getBillItemsOfOrder(String orderCode) throws JsonProcessingException {
        return receiptBillItemRepo.search(orderCode);
    }

    @Override
    public ReturnPaginationDTO<ReceiptBillItem> getBillItemsOfOrder(int page, int pageSize, String sortField, boolean isSortAsc, String orderCode) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<ReceiptBillItem> receiptBillItems = receiptBillItemRepo.searchItems(pageable, orderCode);
        return getPaginationResult(receiptBillItems.getContent(), page, receiptBillItems.getTotalPages(), receiptBillItems.getTotalElements());
    }

    @Override
    public ReceiptBill getBillById(long id) {
        return null;
    }

    @Override
    public ReceiptBill getBillByCode(String code) {
        return null;
    }

    @Override
    public ReceiptBillItem getBillItemsOfOrder(String billCode, String billItemSeq) {
        return null;
    }

    @Override
    public void deleteBillItem(long id) {

    }
}
