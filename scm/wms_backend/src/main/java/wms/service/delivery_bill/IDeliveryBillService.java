package wms.service.delivery_bill;

import com.fasterxml.jackson.core.JsonProcessingException;
import wms.dto.ReturnPaginationDTO;
import wms.entity.DeliveryBill;
import wms.entity.DeliveryBillItem;

import java.util.List;

public interface IDeliveryBillService {
    ReturnPaginationDTO<DeliveryBill> getAllBills(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    List<DeliveryBillItem> getBillItemsOfOrder(String orderCode) throws JsonProcessingException;
    DeliveryBill getBillById(long id);
    DeliveryBill getBillByCode(String code);
    DeliveryBillItem getBillItemsOfOrder(String billCode, String billItemSeq);
    //    void updateBillItem(UpdatePurchaseOrderDTO updatePurchaseOrderDTO, long id) throws CustomException;
    void deleteBillItem(long id);
}
