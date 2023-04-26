package wms.service.delivery_bill;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.bill.SplitBillDTO;
import wms.entity.DeliveryBill;
import wms.entity.DeliveryBillItem;
import wms.entity.ExportInventoryItem;
import wms.entity.ShipmentItem;
import wms.exception.CustomException;
import wms.repo.DeliveryBillItemRepo;
import wms.repo.DeliveryBillRepo;
import wms.repo.ExportInventoryItemRepo;
import wms.service.BaseService;
import wms.utils.GeneralUtils;

import java.util.List;

@Service
@Slf4j
public class DeliveryBillServiceImpl extends BaseService implements IDeliveryBillService {
    private final ExportInventoryItemRepo exportInventoryItemRepo;
    private final DeliveryBillItemRepo deliveryBillItemRepo;
    private final DeliveryBillRepo deliveryBillRepo;

    public DeliveryBillServiceImpl(DeliveryBillRepo deliveryBillRepo,
                                   DeliveryBillItemRepo deliveryBillItemRepo,
                                   ExportInventoryItemRepo exportInventoryItemRepo) {
        this.deliveryBillRepo = deliveryBillRepo;
        this.deliveryBillItemRepo = deliveryBillItemRepo;
        this.exportInventoryItemRepo = exportInventoryItemRepo;
    }

    @Override
    public ReturnPaginationDTO<DeliveryBill> getAllBills(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<DeliveryBill> deliveryBills = deliveryBillRepo.search(pageable);
        return getPaginationResult(deliveryBills.getContent(), page, deliveryBills.getTotalPages(), deliveryBills.getTotalElements());
    }

    @Override
    public List<DeliveryBillItem> getBillItemsOfOrder(String orderCode) throws JsonProcessingException {
        return deliveryBillItemRepo.search(orderCode);
    }

    @Override
    public List<DeliveryBillItem> getBillItemsOfBill(String billCode) throws JsonProcessingException {
        return deliveryBillItemRepo.getAllItemOfABill(billCode);
    }

    @Override
    public ReturnPaginationDTO<DeliveryBill> getBillsCanDeliver(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<DeliveryBill> deliveryBills = deliveryBillRepo.search(pageable);
        return getPaginationResult(deliveryBills.getContent(), page, deliveryBills.getTotalPages(), deliveryBills.getTotalElements());
    }

    @Override
    public DeliveryBill getBillById(long id) {
        return null;
    }

    @Override
    public DeliveryBill getBillByCode(String code) {
        return null;
    }

    @Override
    public DeliveryBillItem getBillItemsOfOrder(String billCode, String billItemSeq) {
        return null;
    }

    @Override
    public void deleteBillItem(long id) {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExportInventoryItem splitBills(SplitBillDTO splitBillDTO) throws CustomException {
        DeliveryBill deliveryBill =  deliveryBillRepo.getBillWithCode(splitBillDTO.getDeliveryBillCode());
        if (deliveryBill == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Can't find referenced bill, can't split");
        }
        ExportInventoryItem newExportInventoryItem = ExportInventoryItem.builder()
                .code("EXINV" + GeneralUtils.generateCodeFromSysTime())
                .quantity(splitBillDTO.getQuantity())
                .deliveryBillItemSeqId(splitBillDTO.getDeliveryBillItemSeqId())
                .deliveryBill(deliveryBill)
                .build();
        return exportInventoryItemRepo.save(newExportInventoryItem);
    }
}
