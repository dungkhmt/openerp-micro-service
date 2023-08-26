package wms.service.facility;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import wms.algorithms.utils.Utils;
import wms.common.enums.CommonStatus;
import wms.common.enums.ErrorCode;
import wms.common.enums.OrderStatus;
import wms.dto.ReturnPaginationDTO;
import wms.dto.facility.*;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.*;
import wms.service.BaseService;
import wms.service.files.IFileService;
import wms.service.purchase_order.IPurchaseOrderService;
import wms.service.sale_order.ISaleOrderService;
import wms.utils.GeneralUtils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FacilityServiceImpl extends BaseService implements IFacilityService {
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private InventoryItemRepo inventoryItemRepo;
    @Autowired
    private ProductUnitRepo productUnitRepo;
    @Autowired
    private DeliveryBillItemRepo deliveryBillItemRepo;
    @Autowired
    private SaleOrderItemRepo saleOrderItemRepo;
    @Autowired
    private DeliveryBillRepo deliveryBillRepo;
    @Autowired
    private SaleOrderRepo saleOrderRepo;
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
    @Autowired
    private IPurchaseOrderService purchaseOrderService;
    @Autowired
    private ISaleOrderService saleOrderService;
    @Autowired
    private IFileService fileService;
    @Override
    public void createFacilityFromFile(MultipartFile file, JwtAuthenticationToken token) throws IOException, CustomException {
        UserRegister createdBy = userRepo.getUserByUserLoginId(token.getName());
        if (createdBy == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this facility, can't create");
        }
        Iterator<Row> rowIterator = fileService.initWorkbookRow(file);
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() == 0 || isRowEmpty(row)) continue;
            saveFacilities(row, createdBy);
        }
    }
    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK)
                return false;
        }
        return true;
    }
    private void saveFacilities(Row row, UserRegister createdBy) throws CustomException {
        List<Facility> listFacilities = new ArrayList<>();
        List<Facility> facilities = facilityRepo.getAllFacility();
        UserRegister manager = userRepo.getUserByUserLoginId(row.getCell(5).toString());
        if (createdBy == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this facility, can't create");
        }
        if (manager == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown manager manage this facility, can't create");
        }
        Facility facility = Facility.builder()
                .name(row.getCell(1).toString())
                .code("FAC" + GeneralUtils.generateCodeFromSysTime() + row.getCell(0).getNumericCellValue())
                .address(row.getCell(2).getStringCellValue())
                .status(CommonStatus.ACTIVE.getStatus().toUpperCase())
                .latitude(row.getCell(3).getStringCellValue())
                .longitude(row.getCell(4).getStringCellValue())
                .creator(createdBy)
                .manager(manager)
                .build();

        listFacilities.add(facility);
        List<Facility> lstFacilities = facilityRepo.saveAll(listFacilities);
        log.info("Num of facilities added {}", listFacilities.size());
        clusterCustomerIntoFacility();
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Facility createFacility(FacilityDTO facilityDTO, JwtAuthenticationToken token) throws CustomException {
        UserRegister createdBy = userRepo.getUserByUserLoginId(token.getName());
        UserRegister manager = userRepo.getUserByUserLoginId(facilityDTO.getManagedBy());
        if (createdBy == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this facility, can't create");
        }
        Facility newFacility = Facility.builder()
                .name(facilityDTO.getName())
                .code("FAC" + GeneralUtils.generateCodeFromSysTime())
                .address(facilityDTO.getAddress())
                .status(CommonStatus.ACTIVE.getStatus().toUpperCase())
                .latitude(facilityDTO.getLatitude())
                .longitude(facilityDTO.getLongitude())
                .creator(createdBy)
                .manager(manager)
                .build();

        Facility facility = facilityRepo.save(newFacility);
        clusterCustomerIntoFacility();
        return facility;
    }

    public void clusterCustomerIntoFacility() {
        List<Customer> customers = customerRepo.getAllCustomers();
        List<Facility> facilities = facilityRepo.getAllFacility();
        for (Customer customer : customers) {
            double bestDistance = Double.POSITIVE_INFINITY;
            int bestIndex = -1;
            for (int i = 0; i < facilities.size(); i++) { 
                double cusLat = Double.parseDouble(customer.getLatitude());
                double cusLon = Double.parseDouble(customer.getLongitude());
                double facLat = Double.parseDouble(facilities.get(i).getLatitude());
                double facLon = Double.parseDouble(facilities.get(i).getLongitude());
                double cusFacDistance = Utils.calculateCoordinationDistance(cusLat, cusLon, facLat, facLon);
                if (cusFacDistance < bestDistance) {
                    bestDistance = cusFacDistance;
                    bestIndex = i;
                }
            }
            customer.setFacility(facilities.get(bestIndex));
            customerRepo.save(customer);
        }

    }
    @Override
    public ReturnPaginationDTO<Facility> getAllFacilities(int page, int pageSize, String sortField, boolean isSortAsc, String facilityName, String status, String createdBy, String managedBy, String textSearch) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<Facility> facilities = facilityRepo.search(pageable, facilityName, status, createdBy, managedBy, textSearch);
        return getPaginationResult(facilities.getContent(), page, facilities.getTotalPages(), facilities.getTotalElements());
    }

    @Override
    public List<Facility> getAllWithoutPaging() {
        return facilityRepo.getAllFacility();
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
    public Facility updateFacility(FacilityUpdateDTO facilityDTO, Long id) throws CustomException {
        Facility facilityToUpdate = facilityRepo.getFacilityById(id);
        if (facilityToUpdate == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Facility does not exist, can't update");
        }
        UserRegister manager = userRepo.getUserByUserLoginId(facilityDTO.getManagedBy());
        if (manager == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Manager unknown");
        }
        facilityToUpdate.setName(facilityDTO.getName());
        facilityToUpdate.setAddress(facilityDTO.getAddress());
        facilityToUpdate.setStatus(facilityDTO.getStatus().toUpperCase());
        facilityToUpdate.setLatitude(facilityDTO.getLatitude());
        facilityToUpdate.setLongitude(facilityDTO.getLongitude());
        facilityToUpdate.setManager(manager);
        Facility facility = facilityRepo.save(facilityToUpdate);
        clusterCustomerIntoFacility();
        return facility;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importToFacilityWithOrder(ImportToFacilityDTO importToFacilityDTO) throws CustomException {
        PurchaseOrder order = purchaseOrderRepo.getOrderByCode(importToFacilityDTO.getOrderCode().toUpperCase());
        if (order == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Can't find referenced order, can't import");
        }
        if (!canImportToFacility(order, importToFacilityDTO)) {
            throw caughtException(ErrorCode.USER_ACTION_FAILED.getCode(), "Exceed warehouse quantity limit, can't import");
        }
        if (isExceedPurchaseOrderQuantity(order, importToFacilityDTO)) {
            throw caughtException(ErrorCode.USER_ACTION_FAILED.getCode(), "Import with quantity more than current order needs, can't import");
        }
        ReceiptBill newBill = ReceiptBill.builder()
                .code("RBIL" + GeneralUtils.generateCodeFromSysTime())
                .purchaseOrder(order)
                .facility(order.getFacility())
                .receiptBillItems(new HashSet<>())  // https://stackoverflow.com/questions/70406298/adding-objects-to-an-array-list-cannot-invoke-xxx-add-because-yyy-is-null
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
            newBill.getReceiptBillItems().add(receiptBillItemRepo.save(item));
            // Lưu trữ thêm phần inventory_item chính là phần nhập kho thực sự (lưu dư thừa để dễ quản lý)
            // Ngoài lưu receipt_bill thì phần này cũng nên lưu.
            InventoryItem inventoryItem = InventoryItem.builder()
                    .code("INV" + GeneralUtils.generateCodeFromSysTime() + "_" + seq) // TODO: Check if there is any other way to set this code
                    .facility(order.getFacility())
                    .lotCode("LOT" + GeneralUtils.generateCodeFromSysTime() + "_" + seq) // TODO: Check if there is any other way to set this code
                    .expireDate(importItem.getExpireDate())
                    .receivingDate(ZonedDateTime.now().toString())
                    .quantity(importItem.getEffectQty())
                    .product(product)
                    .build();
            inventoryItemRepo.save(inventoryItem);
            // TODO: Should not save every inventory item, save by batch or group product with one update.
            // Should have better way to get product facility here.
            ProductFacility productFacility = productFacilityRepo.findInventoryInFacility(order.getFacility().getCode().toUpperCase(), product.getCode());
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
        receiptBillRepo.save(newBill);
        PurchaseOrder currOrder = purchaseOrderRepo.getOrderByCode(importToFacilityDTO.getOrderCode().toUpperCase());
        if (isFinishedImporting(currOrder)) {
            purchaseOrderService.updateOrderStatus(OrderStatus.DELIVERING.getStatus(), order.getCode());
        }
    }

    @Override
    public ReceiptBill getReceiptBillForOrderByCode(String orderCode, String code) {
        return receiptBillRepo.getBillOfOrderByCode(orderCode.toUpperCase(), code.toUpperCase());
    }

    private boolean canImportToFacility(PurchaseOrder currentImportingOrder, ImportToFacilityDTO importToFacilityDTO) {
        Facility currFacility = currentImportingOrder.getFacility();
        for (ProductFacility productInventory : currFacility.getProductFacilities()) {
            for (ImportItemDTO importItem : importToFacilityDTO.getImportItems()) {
                if (importItem.getProductCode().equals(productInventory.getProduct().getCode().toUpperCase())) {
                    int inventoryQty = productInventory.getInventoryQty();
                    int effectiveQty = importItem.getEffectQty();
                    // Exceed threshold
                    if (effectiveQty + inventoryQty > productInventory.getQtyThreshold()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    private boolean isExceedPurchaseOrderQuantity(PurchaseOrder currentImportingOrder, ImportToFacilityDTO importToFacilityDTO) {
        List<ReceiptBill> receiptBills = currentImportingOrder.getReceiptBills();
        Map<String, Integer> qtyMappingFromBill = new HashMap<>();
        for (ReceiptBill bill : receiptBills) {
            for (ReceiptBillItem item : bill.getReceiptBillItems()) {
                // Tồn tại bill của item này rồi => Gộp quantity để so sánh
                if (qtyMappingFromBill.containsKey(item.getProduct().getCode())) {
                    qtyMappingFromBill.merge(item.getProduct().getCode(), item.getEffectiveQty(), Integer::sum);
                }
                // If chưa tồn tại, tạo 1 mapping mới
                else {
                    qtyMappingFromBill.put(item.getProduct().getCode(), item.getEffectiveQty());
                }
            }
        }
        for (PurchaseOrderItem orderItem : currentImportingOrder.getPurchaseOrderItems()) {
            String currentProductCode = orderItem.getProduct().getCode().toUpperCase();
            for (ImportItemDTO importItem : importToFacilityDTO.getImportItems()) {
                if (importItem.getProductCode().equals(currentProductCode)) {
                    // Nếu có mapping của product này rồi thì so sánh xem lượng nhập vào có quá so với order cần hay không
                    if (qtyMappingFromBill.containsKey(orderItem.getProduct().getCode().toUpperCase())) {
                        int mappingQty = qtyMappingFromBill.get(orderItem.getProduct().getCode().toUpperCase());
                        int compareQty = mappingQty + importItem.getEffectQty();
                        if (compareQty > orderItem.getQuantity()) return true;
                    }
                    // Nếu chưa có mapping, nghĩa là đây là 1 product mới hoàn toàn, cần so sánh trực tiếp xem cái bill sắp
                    // được nhập có vượt quá so với order cần hay ko.
                    if (importItem.getEffectQty() > orderItem.getQuantity()) return true;
                }
            }
        }
        return false;
    }

    private boolean isFinishedImporting(PurchaseOrder currOrder) {
        List<ReceiptBill> receiptBills = receiptBillRepo.getAllBillOfOrder(currOrder.getCode().toUpperCase());
        if (receiptBills.size() == 0) return false;
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
            // 1 product khác chưa có bill nhưng có order mới nên việc import rõ ràng chưa hết
            if (!qtyMappingFromBill.containsKey(currentProductCode)) {
                return false;
            }
            int compareQty = qtyMappingFromBill.get(currentProductCode);
            if (compareQty != orderItem.getQuantity()) return false;
        }
        return true;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exportFromFacility(ExportFromFacilityDTO exportFromFacilityDTO) throws CustomException {
        SaleOrder order = saleOrderRepo.getOrderByCode(exportFromFacilityDTO.getOrderCode().toUpperCase());
        if (order == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Can't find referenced order, can't export");
        }
        if (!canExportFromFacility(order, exportFromFacilityDTO)) {
            throw caughtException(ErrorCode.USER_ACTION_FAILED.getCode(), "Exceed warehouse quantity limit, can't export");
        }
        if (isBeyondSaleOrderQty(order, exportFromFacilityDTO)) {
            throw caughtException(ErrorCode.USER_ACTION_FAILED.getCode(), "Export with quantity more than current order needs, can't export");
        }
        DeliveryBill newBill = DeliveryBill.builder()
                .code("DBIL" + GeneralUtils.generateCodeFromSysTime())
                .saleOrder(order)
                .deliveryBillItems(new HashSet<>())
                .deliveryDate(ZonedDateTime.now())
                .build();
        deliveryBillRepo.save(newBill);
        int seq = 0;
        for (ExportItemDTO importItem : exportFromFacilityDTO.getExportItems()) {
            seq++;
            ProductEntity product = productRepo.getProductByCode(importItem.getProductCode().toUpperCase());
            if (product == null) {
                log.error("Product with code {} not found", importItem.getProductCode().toUpperCase());
                throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product not found");
            }
            SaleOrderItem orderItem = saleOrderItemRepo.getItemByProductCode(order.getCode().toUpperCase(), product.getCode().toUpperCase());
            DeliveryBillItem item = DeliveryBillItem.builder()
                    .deliveryBill(newBill)
                    .seqId("00" + seq)
                    .orderSeqId(orderItem.getSeqId())
                    .product(product)
                    .saleOrder(order)
                    .effectiveQty(importItem.getEffectQty())
                    .build();
            newBill.getDeliveryBillItems().add(deliveryBillItemRepo.save(item));
            // TODO: Should not save every inventory item, save by batch or group product with one update.
            ProductFacility productFacility = productFacilityRepo.findInventoryInFacility(order.getCustomer().getFacility().getCode().toUpperCase(), product.getCode());
            productFacility.setInventoryQty(productFacility.getInventoryQty() - importItem.getEffectQty());
            productFacilityRepo.save(productFacility);
        }
        deliveryBillRepo.save(newBill);
        SaleOrder currOrder = saleOrderRepo.getOrderByCode(exportFromFacilityDTO.getOrderCode().toUpperCase());
        if (isFinishedExporting(currOrder)) {
            saleOrderService.updateOrderStatus(OrderStatus.DELIVERING.getStatus(), order.getCode());
        }
    }
    private boolean canExportFromFacility(SaleOrder currExportOrder, ExportFromFacilityDTO exportFromFacilityDTO) {
        Facility currFacility = currExportOrder.getCustomer().getFacility();
        for (ExportItemDTO exportItem : exportFromFacilityDTO.getExportItems()) {
            // Tại sao lại có cái check này? Ví dụ 1 export Item mới mà ko có trong kho -> ProductFacility sẽ ko có
            // Sản phẩm này nên khi check ở vòng for bên dưới sẽ chạy qua luôn, ko return.
            // Do đó để đảm bảo, nếu sản cần xuất ko có trong kho thì rõ ràng không thể xuất đc.
            boolean isExportItemExistInFacility = false;
            for (ProductFacility productInventory : currFacility.getProductFacilities()) {
                if (exportItem.getProductCode().equals(productInventory.getProduct().getCode().toUpperCase())) {
                    int inventoryQty = productInventory.getInventoryQty();
                    int effectiveQty = exportItem.getEffectQty();
                    // Not enough
                    if (effectiveQty > inventoryQty) {
                        return false;
                    }
                    isExportItemExistInFacility = true;
                }
            }
            if (!isExportItemExistInFacility) return false;
        }
        return true;
    }
    private boolean isBeyondSaleOrderQty(SaleOrder currExportingOrder, ExportFromFacilityDTO exportFromFacilityDTO) {
        List<DeliveryBill> deliveryBills = currExportingOrder.getDeliveryBills();
        Map<String, Integer> qtyMappingFromBill = new HashMap<>();
        for (DeliveryBill bill : deliveryBills) {
            for (DeliveryBillItem item : bill.getDeliveryBillItems()) {
                if (item.getDeleted() == 1) continue;
                if (qtyMappingFromBill.containsKey(item.getProduct().getCode())) {
                    qtyMappingFromBill.merge(item.getProduct().getCode(), item.getEffectiveQty(), Integer::sum);
                }
                else {
                    qtyMappingFromBill.put(item.getProduct().getCode(), item.getEffectiveQty());
                }
            }
        }
        for (SaleOrderItem orderItem : currExportingOrder.getSaleOrderItems().stream().filter(item -> item.getDeleted() != 1).collect(Collectors.toList())) {
            String currentProductCode = orderItem.getProduct().getCode().toUpperCase();
            for (ExportItemDTO exportItem : exportFromFacilityDTO.getExportItems()) {
                if (exportItem.getProductCode().equals(currentProductCode)) {
                    if (qtyMappingFromBill.containsKey(orderItem.getProduct().getCode().toUpperCase())) {
                        int mappingQty = qtyMappingFromBill.get(orderItem.getProduct().getCode().toUpperCase());
                        int compareQty = mappingQty + exportItem.getEffectQty();
                        if (compareQty > orderItem.getQuantity()) return true;
                    }
                    // Nếu như ko có cái mapping nào, nghĩa là sản phẩm này mới, chỉ cần check cái số lượng của item
                    // Xuất kho so với order gốc.
                    if (exportItem.getEffectQty() > orderItem.getQuantity()) return true;
                }
            }
        }
        return false;
    }
    private boolean isFinishedExporting(SaleOrder currOrder) {
        List<DeliveryBill> deliveryBills = deliveryBillRepo.getAllBillOfOrder(currOrder.getCode().toUpperCase());
        if (deliveryBills.size() == 0) return false;
        Map<String, Integer> qtyMappingFromBill = new HashMap<>();
        for (DeliveryBill bill : deliveryBills) {
            for (DeliveryBillItem item : bill.getDeliveryBillItems()) {
                if (qtyMappingFromBill.containsKey(item.getProduct().getCode())) {
                    qtyMappingFromBill.merge(item.getProduct().getCode(), item.getEffectiveQty(), Integer::sum);
                }
                else {
                    qtyMappingFromBill.put(item.getProduct().getCode(), item.getEffectiveQty());
                }
            }
        }
        for (SaleOrderItem orderItem : currOrder.getSaleOrderItems()) {
            String currentProductCode = orderItem.getProduct().getCode().toUpperCase();
            // TODO: có cần check tương tự giống phần importing hay không?
            if (!qtyMappingFromBill.containsKey(currentProductCode)) {
                return false;
            }
            int compareQty = qtyMappingFromBill.get(currentProductCode);
            if (compareQty != orderItem.getQuantity()) return false;
        }
        return true;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFacilityById(long id) {
        facilityRepo.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignStaff(String staffCode, String facilityCode) throws CustomException {
        UserRegister userLogin = userRepo.getUserByUserLoginId(staffCode);
        if (userLogin == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff to assign this facility, can't create");
        }
        Facility facility = facilityRepo.getFacilityByCode(facilityCode);
        facility.setManager(userLogin);
        facilityRepo.save(facility);
    }

    @Override
    public List<Customer> getFacilityCustomer(String facilityCode) {
        return customerRepo.getFacilityCustomers(facilityCode);
    }

    @Override
    public ReturnPaginationDTO<Customer> getAllFacilityCustomer(int page, int pageSize, String sortField, boolean isSortAsc, String facilityCode) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<Customer> facilityCustomers = customerRepo.getFacilityCustomersPaging(pageable, facilityCode);
        return getPaginationResult(facilityCustomers.getContent(), page, facilityCustomers.getTotalPages(), facilityCustomers.getTotalElements());
    }
}
