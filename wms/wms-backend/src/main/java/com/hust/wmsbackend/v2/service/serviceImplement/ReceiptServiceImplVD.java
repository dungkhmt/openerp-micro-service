package com.hust.wmsbackend.v2.service.serviceImplement;

import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.entity.enumentity.CurrencyUom;
import com.hust.wmsbackend.management.entity.enumentity.ReceiptStatus;
import com.hust.wmsbackend.management.model.ReceiptRequest;
import com.hust.wmsbackend.management.model.response.*;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.v2.repo.*;
import com.hust.wmsbackend.v2.service.ProductService;
import com.hust.wmsbackend.v2.service.ReceiptService;
import com.hust.wmsbackend.management.service.WarehouseService;
import com.hust.wmsbackend.management.utils.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ReceiptServiceImplVD implements ReceiptService {

    private ReceiptRepository receiptRepository;
    private ReceiptItemRepository2 receiptItemRepository;
    private ReceiptItemRequestRepository receiptItemRequestRepository;
    private InventoryItemRepository inventoryItemRepository;
    private ProductWarehouseRepository productWarehouseRepository;
    private WarehouseRepository warehouseRepository;
    private ProductV2Repository2 productRepository;
    private ReceiptBillRepository receiptBillRepository;

    private ProductService productService;
    private WarehouseService warehouseService;

    @Override
    @Transactional
    public Receipt createReceipt(Principal principal, ReceiptRequest request) {
        log.info(String.format("Start create receipt: ", request));
        List<ReceiptRequest.ReceiptItemRequest> receiptList = request.getReceiptItemList();
        if (receiptList.isEmpty()) {
            log.warn("Receipt item list is empty... Nothing to import");
            return null;
        } else {
            log.info(String.format("Receipt item list: ", receiptList));
        }

        Receipt receipt = Receipt.builder()
                                 .receiptId(UUID.randomUUID())
                                 .warehouseId(request.getWarehouseId() == null ? null : UUID.fromString(request.getWarehouseId()))
                                 .receiptDate(request.getReceivedDate())
                                 .receiptName(request.getReceiptName())
                                 .status(request.getIsPurchaseManagerRequest() == 1 ? ReceiptStatus.APPROVED : ReceiptStatus.CREATED)
                                 .createdReason(request.getCreatedReason())
                                 .expectedReceiptDate(request.getExpectedReceiveDate())
                                 .createdBy(principal.getName())
                                 .build();
        receiptRepository.save(receipt);

        List<ReceiptItemRequest> receiptItemList = receiptList.stream()
            .map(item -> ReceiptItemRequest.builder()
                .receiptItemRequestId(UUID.randomUUID())
                .receiptId(receipt.getReceiptId())
                .productId(UUID.fromString(item.getProductId()))
                .quantity(item.getQuantity())
                .warehouseId(item.getWarehouseId() == null ? null : UUID.fromString(item.getWarehouseId()))
                .build())
            .collect(Collectors.toList());
        receiptItemRequestRepository.saveAll(receiptItemList);

        return receipt;
    }

    @Override
    public List<ReceiptGeneralResponse> getAllReceiptGeneral() {
        List<Receipt> receipts = receiptRepository.findAllByOrderByCreatedStampDesc();
        String pattern = "dd-MM-yyyy HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Map<String, String> warehouseIdNameMap = new HashMap<>();
        for (Receipt receipt : receipts) {
            UUID warehouseId = receipt.getWarehouseId();
            Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);
            if (warehouseOpt.isPresent()) {
                warehouseIdNameMap.put(receipt.getWarehouseId().toString(), warehouseOpt.get().getName());
            } else {
                log.warn(String.format("Not found warehouse with id %s", warehouseId));
            }
        }
        List<ReceiptGeneralResponse> response = receipts.stream()
                                                        .map(r -> ReceiptGeneralResponse.builder()
                                                            .receiptName(r.getReceiptName())
                                                            .createdDate(format.format(r.getCreatedStamp()))
                                                            .warehouseName(warehouseIdNameMap.get(r.getWarehouseId().toString()))
                                                            .receiptId(r.getReceiptId().toString())
                                                            .receivedDate(format.format(r.getReceiptDate()))
                                                            .build())
                                                        .collect(Collectors.toList());
        return response;
    }

    @Override
    public ReceiptRequest getById(String id) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(UUID.fromString(id));
        if (!receiptOpt.isPresent()) {
            log.warn(String.format("Receipt id %s is not found....", id));
            return null;
        }

        Receipt receipt = receiptOpt.get();
        List<ReceiptItem> receiptItems = receiptItemRepository.findAllByReceiptId(receipt.getReceiptId());
        List<ReceiptRequest.ReceiptItemRequest> receiptItemRequestList = receiptItems.stream()
            .map(item -> ReceiptRequest.ReceiptItemRequest
                .builder()
                .productId(item.getProductId().toString())
                .lotId(item.getLotId())
                .bayId(item.getBayId().toString())
                .quantity(item.getQuantity())
                .importPrice(item.getImportPrice())
                .expiredDate(item.getExpiredDate())
                .build())
            .collect(Collectors.toList());
        return ReceiptRequest
            .builder()
            .receiptName(receipt.getReceiptName())
            .receivedDate(receipt.getReceiptDate())
            .warehouseId(receipt.getWarehouseId().toString())
            .description(receipt.getDescription())
            .receiptItemList(receiptItemRequestList)
            .build();
    }

    @Override
    public List<ReceiptRequestResponse> getAllForSaleManagement(String statusCode) {
        ReceiptStatus status = ReceiptStatus.findByCode(statusCode);
        List<Receipt> receipts;
        if (status != null) {
            receipts = receiptRepository.findAllByStatusOrderByCreatedStampDesc(status);
        } else {
            receipts = receiptRepository.findAllByOrderByCreatedStampDesc();
        }
        return receipts.stream()
                .map(this::mapToReceiptRequestResponse)
                .collect(Collectors.toList());
    }

    private ReceiptRequestResponse mapToReceiptRequestResponse(Receipt receipt) {
        return ReceiptRequestResponse.builder()
                .receiptRequestId(receipt.getReceiptId())
                .approvedBy(receipt.getApprovedBy())
                .createdDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY, receipt.getCreatedStamp()))
                .createdBy(receipt.getCreatedBy())
                .status(receipt.getStatus().getName())
                .expectedReceiveDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY, receipt.getExpectedReceiptDate()))
                .build();
    }

    @Override
    public ReceiptRequestResponse getForSaleManagementById(String id) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(UUID.fromString(id));
        return receiptOpt.map(receipt -> {
            List<ReceiptItemRequest> items = receiptItemRequestRepository.findAllByReceiptId(receipt.getReceiptId());
            List<ReceiptRequestResponse.ReceiptRequestItemResponse> itemResponse = items.stream()
                    .map(item -> {
                        ReceiptRequestResponse.ReceiptRequestItemResponse temp = ReceiptRequestResponse.ReceiptRequestItemResponse
                                .builder()
                                .receiptRequestItemId(item.getReceiptItemRequestId())
                                .quantity(item.getQuantity())
                                .productId(item.getProductId())
                                .productName(productRepository.findById(item.getProductId()).get().getName())
                                .warehouseId(item.getWarehouseId())
                                .build();
                        if (item.getWarehouseId() != null) {
                            temp.setWarehouseName(warehouseRepository.findById(item.getWarehouseId()).get().getName());
                        }
                        return temp;
                    })
                    .collect(Collectors.toList());

            return ReceiptRequestResponse.builder()
                    .receiptRequestId(receipt.getReceiptId())
                    .createdDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY, receipt.getCreatedStamp()))
                    .approvedBy(receipt.getApprovedBy())
                    .status(receipt.getStatus().getName())
                    .createdBy(receipt.getCreatedBy())
                    .createdReason(receipt.getCreatedReason())
                    .expectedReceiveDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY, receipt.getExpectedReceiptDate()))
                    .items(itemResponse)
                    .build();
        }).orElseGet(() -> {
            log.warn(String.format("Receipt id %s not present", id));
            return null;
        });
    }

    @Override
    public boolean approve(Principal principal, String id) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(UUID.fromString(id));
        if (!receiptOpt.isPresent()) {
            log.warn(String.format("Receipt %s is not present", id));
            return false;
        }
        Receipt receipt = receiptOpt.get();
        if (receipt.getStatus() != ReceiptStatus.CREATED) {
            log.warn("Receipt status is not CREATED");
            return false;
        }
        receipt.setApprovedBy(principal.getName());
        receipt.setStatus(ReceiptStatus.APPROVED);
        receiptRepository.save(receipt);
        return true;
    }

    @Override
    public ReceiptProcessResponse getForProcessingById(String id) {
        UUID receiptId = UUID.fromString(id);
        Optional<Receipt> receiptOpt = receiptRepository.findById(receiptId);
        if (!receiptOpt.isPresent()) {
            log.warn(String.format("Receipt %s is not present", id));
            throw new RuntimeException("Receipt does not exist");
        }

        // Danh sách các mục đã được xử lý
        List<ProcessedItemModel> processedItems = receiptItemRepository.getProcessedItemsByReceiptId(receiptId);

        // Tạo một bản đồ để theo dõi số lượng mỗi mục sản phẩm đã được xử lý
        Map<UUID, BigDecimal> processedItemsCount = new HashMap<>();
        for (ProcessedItemModel item : processedItems) {
            UUID key = item.getReceiptItemRequestId();
            BigDecimal processedQuantity = processedItemsCount.getOrDefault(key, BigDecimal.ZERO);
            processedItemsCount.put(key, processedQuantity.add(item.getQuantity()));
        }

        // Lấy thông tin về tên sản phẩm và tên kho hàng
        Map<UUID, String> productMap = productService.getProductNameMap();
        Map<UUID, String> warehouseMap = warehouseService.getWarehouseNameMap();

        List<ReceiptItemRequest> requestItems = receiptItemRequestRepository.findAllByReceiptId(receiptId);
        List<ReceiptProcessResponse.RemainingItemResponse> remainingItems = new ArrayList<>();
        for (ReceiptItemRequest item : requestItems) {
            UUID productId = item.getProductId();
            UUID requestId = item.getReceiptItemRequestId();
            BigDecimal processedCount = processedItemsCount.getOrDefault(requestId, BigDecimal.ZERO);
            BigDecimal remainingCount = item.getQuantity().subtract(processedCount);
            if (remainingCount.compareTo(BigDecimal.ZERO) > 0) {
                remainingItems.add(ReceiptProcessResponse.RemainingItemResponse.builder()
                        .receiptItemRequestId(requestId)
                        .productId(productId)
                        .productName(productMap.getOrDefault(productId, null))
                        .warehouseId(item.getWarehouseId())
                        .warehouseName(warehouseMap.getOrDefault(item.getWarehouseId(), null))
                        .quantity(remainingCount)
                        .build());
            }
        }

        return ReceiptProcessResponse.builder()
                .info(receiptOpt.get())
                .processedItems(processedItems)
                .remainingItems(remainingItems)
                .build();
    }

    @Override
    @Transactional
    public boolean process(String receiptId, List<ProcessedItemModel> items, boolean isDone, Principal principal) {
        if (items == null) {
            log.warn("No receipt item processed!");
            return false;
        }
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (ProcessedItemModel processedItem : items) {
            totalPrice = totalPrice.add(processedItem.getQuantity().multiply(processedItem.getImportPrice()));
        }
        try {
            ReceiptBill bill = createReceiptBill(receiptId, totalPrice, principal);
            String billId = bill.getReceiptBillId();
            updateReceiptStatus(receiptId, isDone);
            updateProcessedItems(items, receiptId, billId);
            return true;
        } catch (Exception e) {
            log.error("Error occurred during processing receipt items: " + e.getMessage());
            return false;
        }
    }

    private ReceiptBill createReceiptBill(String receiptId, BigDecimal totalPrice, Principal principal) {
        ReceiptBill bill = ReceiptBill.builder()
                .receiptId(UUID.fromString(receiptId))
                .totalPrice(totalPrice)
                .createdBy(principal.getName())
                .build();
        return receiptBillRepository.save(bill);
    }

    private void updateReceiptStatus(String receiptId, boolean isDone) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(UUID.fromString(receiptId));
        receiptOpt.ifPresent(receipt -> {
            receipt.setStatus(isDone ? ReceiptStatus.COMPLETED : ReceiptStatus.IN_PROGRESS);
            receiptRepository.save(receipt);
        });
    }

    private void updateProcessedItems(List<ProcessedItemModel> items, String receiptId, String billId) {
        for (ProcessedItemModel processedItem : items) {
            try {
                // Create receipt_item record
                ReceiptItem receiptItem = createReceiptItem(processedItem, receiptId, billId);
                receiptItemRepository.save(receiptItem);

                // Create inventory_item
                InventoryItem inventoryItem = createInventoryItem(processedItem);
                inventoryItemRepository.save(inventoryItem);

                // Create/update product_warehouse
                updateProductWarehouse(processedItem);
            } catch (Exception e) {
                log.error("Error occurred during processing item: " + e.getMessage());
            }
        }
    }

    private ReceiptItem createReceiptItem(ProcessedItemModel processedItem, String receiptId, String billId) {
        return ReceiptItem.builder()
                .receiptItemId(UUID.randomUUID())
                .receiptId(UUID.fromString(receiptId))
                .productId(processedItem.getProductId())
                .quantity(processedItem.getQuantity())
                .bayId(processedItem.getBayId())
                .lotId(processedItem.getLotId())
                .importPrice(processedItem.getImportPrice())
                .receiptItemRequestId(processedItem.getReceiptItemRequestId())
                .receiptBillId(billId)
                .build();
    }

    private InventoryItem createInventoryItem(ProcessedItemModel processedItem) {
        return InventoryItem.builder()
                .inventoryItemId(UUID.randomUUID())
                .productId(processedItem.getProductId())
                .lotId(processedItem.getLotId())
                .warehouseId(processedItem.getWarehouseId())
                .bayId(processedItem.getBayId())
                .quantityOnHandTotal(processedItem.getQuantity())
                .importPrice(processedItem.getImportPrice())
                .currencyUomId(CurrencyUom.VND.getName())
                .datetimeReceived(new Date())
                .build();
    }

    private void updateProductWarehouse(ProcessedItemModel processedItem) {
        Optional<ProductWarehouse> productWarehouseOpt = productWarehouseRepository
                .findProductWarehouseByWarehouseIdAndProductId(processedItem.getWarehouseId(), processedItem.getProductId());
        if (productWarehouseOpt.isPresent()) {
            ProductWarehouse productWarehouse = productWarehouseOpt.get();
            BigDecimal newQuantity = productWarehouse.getQuantityOnHand().add(processedItem.getQuantity());
            productWarehouse.setQuantityOnHand(newQuantity);
            productWarehouseRepository.save(productWarehouse);
        } else {
            ProductWarehouse productWarehouse = ProductWarehouse.builder()
                    .productWarehouseId(UUID.randomUUID())
                    .warehouseId(processedItem.getWarehouseId())
                    .productId(processedItem.getProductId())
                    .quantityOnHand(processedItem.getQuantity())
                    .build();
            productWarehouseRepository.save(productWarehouse);
        }
    }

    @Override
    public boolean cancel(Principal principal, String id) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(UUID.fromString(id));
        if (!receiptOpt.isPresent()) {
            log.warn(String.format("Receipt %s is not present", id));
            return false;
        }
        Receipt receipt = receiptOpt.get();
        receipt.setCancelledBy(principal.getName());
        receipt.setStatus(ReceiptStatus.CANCELLED);
        receiptRepository.save(receipt);
        return true;
    }

    @Override
    public List<ReceiptBillWithItems> getReceiptBills(String receiptId) {
        List<ReceiptBill> bills;
        if (receiptId == null) {
            bills = receiptBillRepository.findAllByOrderByCreatedStampDesc();
        } else {
            bills = receiptBillRepository.findAllByReceiptId(UUID.fromString(receiptId));
        }

        return bills.stream()
                .map(ReceiptBillWithItems::new)
                .collect(Collectors.toList());
    }

    private void updateInventoryItem(
        ReceiptRequest request,
        ReceiptRequest.ReceiptItemRequest item,
        UUID productId,
        UUID bayId,
        UUID warehouseId
    ) {
        InventoryItem inventoryItem = InventoryItem.builder()
                                                   .inventoryItemId(UUID.randomUUID())
                                                   .productId(productId)
                                                   .lotId(item.getLotId())
                                                   .warehouseId(warehouseId)
                                                   .bayId(bayId)
                                                   .quantityOnHandTotal(item.getQuantity())
                                                   .importPrice(item.getImportPrice())
                                                   .currencyUomId("VND")
                                                   .datetimeReceived(request.getReceivedDate())
                                                   .expireDate(item.getExpiredDate())
                                                   .description(request.getDescription())
                                                   .build();
        inventoryItemRepository.save(inventoryItem);
        log.info(String.format("Saved / updated an inventory item with id %s", inventoryItem.getInventoryItemId()));
    }

    private void updateProductWarehouse(ReceiptRequest.ReceiptItemRequest item, UUID productId, UUID warehouseId) {
        Optional<ProductWarehouse> productWarehouseOpt = productWarehouseRepository
            .findProductWarehouseByWarehouseIdAndProductId(warehouseId, productId);
        ProductWarehouse productWarehouse;
        if (productWarehouseOpt.isPresent()) {
            productWarehouse = productWarehouseOpt.get();
            BigDecimal newQuantity = productWarehouse.getQuantityOnHand().add(item.getQuantity());
            productWarehouse.setQuantityOnHand(newQuantity);
        } else {
            productWarehouse = ProductWarehouse.builder()
                                               .productWarehouseId(UUID.randomUUID())
                                               .warehouseId(warehouseId)
                                               .productId(productId)
                                               .quantityOnHand(item.getQuantity())
                                               .build();
        }
        productWarehouseRepository.save(productWarehouse);
        log.info(String.format("Saved / updated product warehouse with id %s", productWarehouse.getProductWarehouseId()));
    }

    @Override
    public List<ReceiptRequestResponse> getListReceiptsForSaleManager(Principal principal, String[] statuses) {
        List<ReceiptStatus> statusCodes = Arrays.stream(statuses)
                .map(ReceiptStatus::findByCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Receipt> receipts = statusCodes.isEmpty() ?
                receiptRepository.findAllByOrderByCreatedStampDesc() :
                receiptRepository.findAllByStatusInOrderByCreatedStampDesc(statusCodes);

        return receipts.stream()
                .map(receipt -> ReceiptRequestResponse.builder()
                        .receiptRequestId(receipt.getReceiptId())
                        .approvedBy(receipt.getApprovedBy())
                        .createdDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY, receipt.getCreatedStamp()))
                        .createdBy(receipt.getCreatedBy())
                        .cancelledBy(receipt.getCancelledBy())
                        .status(receipt.getStatus().getName())
                        .lastUpdateStamp(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY, receipt.getLastUpdatedStamp()))
                        .expectedReceiveDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY, receipt.getExpectedReceiptDate()))
                        .build())
                .collect(Collectors.toList());
    }
}
