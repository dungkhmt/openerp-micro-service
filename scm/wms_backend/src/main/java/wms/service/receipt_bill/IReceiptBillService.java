package wms.service.receipt_bill;

import com.fasterxml.jackson.core.JsonProcessingException;
import wms.dto.ReturnPaginationDTO;
import wms.entity.ReceiptBill;
import wms.entity.ReceiptBillItem;

import java.util.List;

public interface IReceiptBillService {
    ReturnPaginationDTO<ReceiptBill> getAllBills(int page, int pageSize, String sortField, boolean isSortAsc, String orderCode) throws JsonProcessingException;
    List<ReceiptBillItem> getBillItemsOfOrder(String orderCode) throws JsonProcessingException;
    ReturnPaginationDTO<ReceiptBillItem> getBillItemsOfOrder(int page, int pageSize, String sortField, boolean isSortAsc, String orderCode) throws JsonProcessingException;
    ReceiptBill getBillById(long id);
    ReceiptBill getBillByCode(String code);
    ReceiptBillItem getBillItemsOfOrder(String billCode, String billItemSeq);
//    void updateBillItem(UpdatePurchaseOrderDTO updatePurchaseOrderDTO, long id) throws CustomException;
    void deleteBillItem(long id);
}
