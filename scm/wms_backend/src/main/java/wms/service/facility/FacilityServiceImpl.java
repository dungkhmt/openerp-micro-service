package wms.service.facility;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.facility.FacilityDTO;
import wms.dto.facility.FacilityUpdateDTO;
import wms.dto.facility.ImportItemDTO;
import wms.dto.facility.ImportToFacilityDTO;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.*;
import wms.service.BaseService;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FacilityServiceImpl extends BaseService implements IFacilityService {
    @Autowired
    private ReceiptBillItemRepo receiptBillItemRepo;
    @Autowired
    private PurchaseOrderItemRepo purchaseOrderItemRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private PurchaseOrderRepo purchaseOrderRepo;
    @Autowired
    private ReceiptBillRepo receiptBillRepo;
    @Autowired
    private ProductFacilityRepo productFacilityRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private FacilityRepo facilityRepo;
    @Override
    public Facility createFacility(FacilityDTO facilityDTO) throws CustomException {
        if (facilityRepo.getFacilityByCode(facilityDTO.getCode()) != null) {
            throw caughtException(ErrorCode.ALREADY_EXIST.getCode(), "Exist customer with same code, can't create");
        }
        UserLogin user = userRepo.getUserByUserLoginId(facilityDTO.getCreatedBy());
        UserLogin manager = userRepo.getUserByUserLoginId(facilityDTO.getManagedBy());
        if (user == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this facility, can't create");
        }
//        if (manager == null) {
//            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this facility, can't create");
//        }
        Facility newFacility = Facility.builder()
                .name(facilityDTO.getName())
                .code(facilityDTO.getCode().toUpperCase())
                .address(facilityDTO.getAddress())
                .status(facilityDTO.getStatus())
                .latitude(facilityDTO.getLatitude())
                .longitude(facilityDTO.getLongitude())
                .creator(user)
                .manager(manager)
                .build();
        return facilityRepo.save(newFacility);
    }

    @Override
    public ReturnPaginationDTO<Facility> getAllFacilities(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<Facility> facilities = facilityRepo.search(pageable);
        return getPaginationResult(facilities.getContent(), page, facilities.getTotalPages(), facilities.getTotalElements());
    }

    @Override
    public Facility getFacilityById(long id) {
        return facilityRepo.getFacilityById(id);
    }

    @Override
    public Facility getFacilityByCode(String code) {
        return facilityRepo.getFacilityByCode(code.toUpperCase());
    }

    @Override
    public ReturnPaginationDTO<ProductFacility> getInventoryItems(int page, int pageSize, String sortField, boolean isSortAsc, String facilityCode) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<ProductFacility> inventory = productFacilityRepo.search(pageable, facilityCode);
        return getPaginationResult(inventory.getContent(), page, inventory.getTotalPages(), inventory.getTotalElements());
    }

    @Override
    public Facility updateFacility(FacilityUpdateDTO facilityDTO, long id) throws CustomException {
        Facility facilityByCode = facilityRepo.getFacilityByCode(facilityDTO.getCode());
        if (facilityByCode != null && facilityByCode.getId() != id) {
            throw caughtException(ErrorCode.ALREADY_EXIST.getCode(), "Exist facility with same code, can't update");
        }
        Facility facilityToUpdate = facilityRepo.getFacilityById(id);
        facilityToUpdate.setName(facilityDTO.getName());
        facilityToUpdate.setCode(facilityToUpdate.getCode());
        facilityToUpdate.setAddress(facilityDTO.getAddress());
        facilityToUpdate.setStatus(facilityDTO.getStatus());
        facilityToUpdate.setLatitude(facilityToUpdate.getLatitude());
        facilityToUpdate.setLongitude(facilityToUpdate.getLongitude());
        // Don't update user_created
        return facilityRepo.save(facilityToUpdate);
    }

    @Override
    public void importToFacility(ImportToFacilityDTO importToFacilityDTO) throws CustomException {
        if (getReceiptBillForOrderByCode(importToFacilityDTO.getOrderCode().toUpperCase(), importToFacilityDTO.getCode().toUpperCase()) != null) {
            throw caughtException(ErrorCode.ALREADY_EXIST.getCode(), "Exist bill with same code, can't create");
        }
        PurchaseOrder order = purchaseOrderRepo.getOrderByCode(importToFacilityDTO.getOrderCode().toUpperCase());
        if (order == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Can't find referenced order, can't create");
        }
        if (!canImportToFacility(order, importToFacilityDTO)) {
            throw caughtException(ErrorCode.USER_ACTION_FAILED.getCode(), "Import with quantity more than current order needs, can't create");
        }
        ReceiptBill newBill = ReceiptBill.builder()
                .code(importToFacilityDTO.getCode().toUpperCase())
                .purchaseOrder(order)
                .facility(order.getFacility())
                .receivingDate(ZonedDateTime.now())
                .build();
        receiptBillRepo.save(newBill);

        int seq = 0;
        for (ImportItemDTO importItem : importToFacilityDTO.getImportItems()) {
            seq++;
            ProductEntity product = productRepo.getProductByCode(importItem.getProductCode().toUpperCase());
            if (product == null) {
                log.error("Product with code {} not found", importItem.getProductCode().toUpperCase());
                throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product not found");
            }
            PurchaseOrderItem orderItem = purchaseOrderItemRepo.getItemByProductCode(order.getCode().toUpperCase(), product.getCode().toUpperCase());
            ReceiptBillItem item = ReceiptBillItem.builder()
                    .receiptBill(newBill)
                    .seqId("00" + seq)
                    .orderSeqId(orderItem.getSeqId())
                    .product(product)
                    .purchaseOrder(order)
                    .receivingDate(ZonedDateTime.now())
                    .effectiveQty(importItem.getEffectQty())
                    .build();
            receiptBillItemRepo.save(item);
            // TODO: Should not save every inventory item, save by batch or group product with one update.
            ProductFacility productFacility = productFacilityRepo.findProductInFacility(order.getFacility().getCode().toUpperCase(), order.getCode());
            if (productFacility == null) {
                productFacility = ProductFacility.builder()
                        .product(product)
                        .inventoryQty(0)
                        .qtyThreshold(1000)
                        .facility(order.getFacility())
                        .build();
            }
            productFacility.setInventoryQty(productFacility.getInventoryQty() + importItem.getEffectQty());
            productFacilityRepo.save(productFacility);
        }
    }

    @Override
    public ReceiptBill getReceiptBillForOrderByCode(String orderCode, String code) {
        return receiptBillRepo.getReceiptBillByCode(orderCode.toUpperCase(), code.toUpperCase());
    }

    private boolean canImportToFacility(PurchaseOrder currentImportingOrder , ImportToFacilityDTO importToFacilityDTO) {
        List<ReceiptBill> receiptBills = currentImportingOrder.getReceiptBills();
        if (receiptBills.size() == 0) return true;
        Map<String, Integer> qtyMappingFromBill = new HashMap<>();
        for (ReceiptBill bill : receiptBills) {
            for (ReceiptBillItem item : bill.getReceiptBillItems()) {
                if (qtyMappingFromBill.containsKey(item.getProduct().getCode())) {
                    qtyMappingFromBill.merge(item.getProduct().getCode(), item.getEffectiveQty(), Integer::sum);
                }
                else {
                    qtyMappingFromBill.put(item.getProduct().getCode(), item.getEffectiveQty());
                }
            }
        }
        for (PurchaseOrderItem orderItem : currentImportingOrder.getPurchaseOrderItems()) {
            String currentProductCode = orderItem.getProduct().getCode().toUpperCase();
            for (ImportItemDTO importItem : importToFacilityDTO.getImportItems()) {
                if (importItem.getProductCode().equals(currentProductCode)) {
                    int compareQty = qtyMappingFromBill.get(orderItem.getProduct().getCode().toUpperCase()) + importItem.getEffectQty();
                    if (compareQty > orderItem.getQuantity()) return false;
                }
            }
        }
        return true;
    }

    private boolean isFinishedImporting(PurchaseOrder currOrder) {
        List<ReceiptBill> receiptBills = currOrder.getReceiptBills();
        Map<String, Integer> qtyMappingFromBill = new HashMap<>();
        for (ReceiptBill bill : receiptBills) {
            for (ReceiptBillItem item : bill.getReceiptBillItems()) {
                if (qtyMappingFromBill.containsKey(item.getProduct().getCode())) {
                    qtyMappingFromBill.merge(item.getProduct().getCode(), item.getEffectiveQty(), Integer::sum);
                }
                else {
                    qtyMappingFromBill.put(item.getProduct().getCode(), item.getEffectiveQty());
                }
            }
        }
        for (PurchaseOrderItem orderItem : currOrder.getPurchaseOrderItems()) {
            String currentProductCode = orderItem.getProduct().getCode().toUpperCase();
            int compareQty = qtyMappingFromBill.get(currentProductCode);
            if (compareQty != orderItem.getQuantity()) return false;
        }
        return true;
    }
    @Override
    public void exportFromFacility() {

    }

    @Override
    public void deleteFacilityById(long id) {
        facilityRepo.deleteById(id);
    }
}
