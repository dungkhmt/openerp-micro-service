package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.entity.enumentity.CurrencyUom;
import com.hust.wmsbackend.management.entity.enumentity.ReceiptStatus;
import com.hust.wmsbackend.management.model.ReceiptRequest;
import com.hust.wmsbackend.management.model.response.ProcessedItemModel;
import com.hust.wmsbackend.management.model.response.ReceiptGeneralResponse;
import com.hust.wmsbackend.management.model.response.ReceiptProcessResponse;
import com.hust.wmsbackend.management.model.response.ReceiptRequestResponse;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.service.ProductService;
import com.hust.wmsbackend.management.service.ReceiptService;
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
public class ReceiptServiceImpl implements ReceiptService {

    private ReceiptRepository receiptRepository;
    private ReceiptItemRepository receiptItemRepository;
    private ReceiptItemRequestRepository receiptItemRequestRepository;
    private InventoryItemRepository inventoryItemRepository;
    private ProductWarehouseRepository productWarehouseRepository;
    private WarehouseRepository warehouseRepository;
    private ProductV2Repository productRepository;

    private ProductService productService;
    private WarehouseService warehouseService;

    @Override
    @Transactional
    public Receipt createReceipt(Principal principal, ReceiptRequest request) {
        log.info(String.format("Start create receipt with request %s", request));
        List<ReceiptRequest.ReceiptItemRequest> receiptList = request.getReceiptItemList();
        if (receiptList.isEmpty()) {
            log.warn("Receipt item list is empty... Nothing to import");
            return null;
        }

        Receipt receipt = Receipt.builder()
                                 .receiptId(UUID.randomUUID())
                                 .warehouseId(request.getWarehouseId() == null ? null : UUID.fromString(request.getWarehouseId()))
                                 .receiptDate(request.getReceivedDate())
                                 .receiptName(request.getReceiptName())
                                 .status(ReceiptStatus.CREATED)
                                 .createdReason(request.getCreatedReason())
                                 .expectedReceiptDate(request.getExpectedReceiveDate())
                                 .createdBy(principal.getName())
                                 // created by set
                                 .build();
        receiptRepository.save(receipt);

        List<ReceiptItemRequest> receiptItemList = receiptList.stream()
            .map(r -> ReceiptItemRequest.builder()
                .receiptItemRequestId(UUID.randomUUID())
                .receiptId(receipt.getReceiptId())
                .productId(UUID.fromString(r.getProductId()))
                .quantity(r.getQuantity())
                .warehouseId(r.getWarehouseId() == null ? null : UUID.fromString(r.getWarehouseId()))
                .build())
            .collect(Collectors.toList());
        receiptItemRequestRepository.saveAll(receiptItemList);

        return receipt;
    }

    @Override
    public List<ReceiptGeneralResponse> getAllReceiptGeneral() {
        List<Receipt> receipts = receiptRepository.findAll();
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
    public List<ReceiptRequestResponse> getForSaleManagement(Principal principal, String[] statusCodesString) {
        List<ReceiptStatus> statusCodes = Arrays.stream(statusCodesString).map(ReceiptStatus::findByCode)
                                                .collect(Collectors.toList());;
        List<Receipt> receipts;
        if (statusCodes.size() != 0) {
            receipts = receiptRepository.findAllByStatusIn(statusCodes);
        } else {
            receipts = receiptRepository.findAll();
        }
        return receipts.stream().map(receipt -> ReceiptRequestResponse.builder()
            .receiptRequestId(receipt.getReceiptId())
            .approvedBy(receipt.getApprovedBy())
            .createdDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, receipt.getCreatedStamp()))
            .createdBy(receipt.getCreatedBy())
            .cancelledBy(receipt.getCancelledBy())
            .status(receipt.getStatus().getName())
            .lastUpdateStamp(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, receipt.getLastUpdatedStamp()))
            .expectedReceiveDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, receipt.getExpectedReceiptDate()))
            .build())
            .collect(Collectors.toList());
    }

    @Override
    public List<ReceiptRequestResponse> getAllForSaleManagement(String statusCode) {
        ReceiptStatus status = ReceiptStatus.findByCode(statusCode);
        List<Receipt> receipts;
        if (status != null) {
            receipts = receiptRepository.findAllByStatus(status);
        } else {
            receipts = receiptRepository.findAll();
        }
        return receipts.stream().map(receipt -> ReceiptRequestResponse.builder()
            .receiptRequestId(receipt.getReceiptId())
            .approvedBy(receipt.getApprovedBy())
            .createdDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, receipt.getCreatedStamp()))
            .createdBy(receipt.getCreatedBy())
            .status(receipt.getStatus().getName())
            .expectedReceiveDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, receipt.getExpectedReceiptDate()))
            .build())
            .collect(Collectors.toList());
    }

    @Override
    public ReceiptRequestResponse getForSaleManagementById(String id) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(UUID.fromString(id));
        if (!receiptOpt.isPresent()) {
            log.warn(String.format("Receipt id %s not present", id));
            return null;
        }
        Receipt receipt = receiptOpt.get();
        List<ReceiptItemRequest> items = receiptItemRequestRepository.findAllByReceiptId(receipt.getReceiptId());
        List<ReceiptRequestResponse.ReceiptRequestItemResponse> itemResponse = new ArrayList<>();
        for (ReceiptItemRequest item : items) {
            ReceiptRequestResponse.ReceiptRequestItemResponse temp = ReceiptRequestResponse.ReceiptRequestItemResponse
                .builder()
                .receiptRequestItemId(item.getReceiptItemRequestId())
                .quantity(item.getQuantity())
                .productId(item.getProductId())
                .productName(productRepository
                                 .findById(item.getProductId())
                                 .get()
                                 .getName())
                .warehouseId(item.getWarehouseId())
                .build();
            if (item.getWarehouseId() != null) {
                temp.setWarehouseName(warehouseRepository
                                          .findById(item.getWarehouseId())
                                          .get()
                                          .getName());
            }
            itemResponse.add(temp);
        }
        return ReceiptRequestResponse.builder()
                .receiptRequestId(receipt.getReceiptId())
                .createdDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, receipt.getCreatedStamp()))
                .approvedBy(receipt.getApprovedBy())
                .status(receipt.getStatus().getName())
                .createdBy(receipt.getCreatedBy())
                .createdReason(receipt.getCreatedReason())
                .expectedReceiveDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, receipt.getExpectedReceiptDate()))
                .items(itemResponse).build();
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
    public ReceiptProcessResponse getForProcessingById(String id) {
        UUID receiptId = UUID.fromString(id);
        Optional<Receipt> receiptOpt = receiptRepository.findById(receiptId);
        if (!receiptOpt.isPresent()) {
            log.warn(String.format("Receipt %s is not present", id));
            throw new RuntimeException("Receipt is not exist");
        }

        List<ProcessedItemModel> processedItems =
            receiptItemRepository.getProcessedItemsByReceiptId(receiptId);
        Map<UUID, BigDecimal> processedProductItemCount = new HashMap<>();
        for (ProcessedItemModel item : processedItems) {
            UUID key = item.getReceiptItemRequestId();
            if (processedProductItemCount.containsKey(key)) {
                BigDecimal prevCount = processedProductItemCount.get(key);
                BigDecimal newCount = prevCount.add(item.getQuantity());
                processedProductItemCount.put(key, newCount);
            } else {
                processedProductItemCount.put(key, item.getQuantity());
            }
        }
        Map<UUID, String> productMap = productService.getProductNameMap();
        Map<UUID, String> warehouseMap = warehouseService.getWarehouseNameMap();
        List<ReceiptItemRequest> requestItems = receiptItemRequestRepository.findAllByReceiptId(receiptId);
        List<ReceiptProcessResponse.RemainingItemResponse> remainingItems = new ArrayList<>();
        for (ReceiptItemRequest item : requestItems) {
            UUID productId = item.getProductId();
            BigDecimal processedCount = processedProductItemCount.getOrDefault(item.getReceiptItemRequestId(), BigDecimal.ZERO);
            BigDecimal totalCount = item.getQuantity();
            BigDecimal remainingCount = totalCount.subtract(processedCount);
            if (remainingCount.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            remainingItems.add(ReceiptProcessResponse.RemainingItemResponse.builder()
                                   .receiptItemRequestId(item.getReceiptItemRequestId())
                                   .productId(productId)
                                   .productName(productMap.getOrDefault(productId, null))
                                   .warehouseId(item.getWarehouseId())
                                   .warehouseName(warehouseMap.getOrDefault(item.getWarehouseId(), null))
                                   .quantity(remainingCount)
                                   .build());
        }
        return ReceiptProcessResponse.builder()
            .info(receiptOpt.get())
            .processedItems(processedItems)
            .remainingItems(remainingItems)
            .build();
    }

    @Override
    @Transactional
    public boolean process(String receiptId, List<ProcessedItemModel> items, boolean isDone) {
        if (items == null) {
            log.warn("Receipt items for processing is null");
            return false;
        }
        for (ProcessedItemModel model : items) {
            try {
                // update receipt status to IN_PROCESS
                Optional<Receipt> receiptOpt = receiptRepository.findById(UUID.fromString(receiptId));
                if (!receiptOpt.isPresent()) {
                    log.warn(String.format("Receipt id %s is not exist", receiptId));
                    return false;
                }
                Receipt receipt = receiptOpt.get();
                receipt.setStatus(isDone ? ReceiptStatus.COMPLETED : ReceiptStatus.IN_PROGRESS);
                receiptRepository.save(receipt);
                // create receipt_item record
                ReceiptItem receiptItem = ReceiptItem
                    .builder()
                    .receiptItemId(UUID.randomUUID())
                    .receiptId(UUID.fromString(receiptId))
                    .productId(model.getProductId())
                    .quantity(model.getQuantity())
                    .bayId(model.getBayId())
                    .lotId(model.getLotId())
                    .importPrice(model.getImportPrice())
                    .receiptItemRequestId(model.getReceiptItemRequestId())
                    .build();;
                receiptItemRepository.save(receiptItem);
                // create inventory_item
                InventoryItem inventoryItem = InventoryItem
                    .builder()
                    .inventoryItemId(UUID.randomUUID())
                    .productId(model.getProductId())
                    .lotId(model.getLotId())
                    .warehouseId(model.getWarehouseId())
                    .bayId(model.getBayId())
                    .quantityOnHandTotal(model.getQuantity())
                    .importPrice(model.getImportPrice())
                    .currencyUomId(CurrencyUom.VND.getName())
                    .datetimeReceived(new Date())
                    .build();
                inventoryItemRepository.save(inventoryItem);
                // create / update product_warehouse
                Optional<ProductWarehouse> productWarehouseOpt = productWarehouseRepository.
                    findProductWarehouseByWarehouseIdAndProductId(model.getWarehouseId(), model.getProductId());
                ProductWarehouse productWarehouse;
                if (productWarehouseOpt.isPresent()) {
                    productWarehouse = productWarehouseOpt.get();
                    BigDecimal newQuantity = productWarehouse.getQuantityOnHand().add(model.getQuantity());
                    productWarehouse.setQuantityOnHand(newQuantity);
                } else {
                    productWarehouse = ProductWarehouse
                        .builder()
                        .productWarehouseId(UUID.randomUUID())
                        .warehouseId(model.getWarehouseId())
                        .productId(model.getProductId())
                        .quantityOnHand(model.getQuantity())
                        .build();
                }
                productWarehouseRepository.save(productWarehouse);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
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
}
