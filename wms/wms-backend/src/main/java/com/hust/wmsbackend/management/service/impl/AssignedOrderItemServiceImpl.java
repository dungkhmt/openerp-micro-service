package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.entity.enumentity.AssignedOrderItemStatus;
import com.hust.wmsbackend.management.model.AssignedOrderItemDTO;
import com.hust.wmsbackend.management.model.DeliveryTripDTO;
import com.hust.wmsbackend.management.model.request.AssignedOrderItemRequest;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.service.AssignedOrderItemService;
import com.hust.wmsbackend.management.service.BayService;
import com.hust.wmsbackend.management.service.ProductService;
import com.hust.wmsbackend.management.service.WarehouseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AssignedOrderItemServiceImpl implements AssignedOrderItemService {

    private AssignedOrderItemRepository assignedOrderItemRepository;
    private InventoryItemRepository inventoryItemRepository;
    private SaleOrderHeaderRepository saleOrderHeaderRepository;
    private CustomerAddressRepository customerAddressRepository;
    private DeliveryTripItemRepository deliveryTripItemRepository;
    private ProductWarehouseRepository productWarehouseRepository;

    private ProductService productService;
    private WarehouseService warehouseService;
    private BayService bayService;

    @Override
    @Transactional
    public boolean create(AssignedOrderItemRequest request) {
        try {
            List<InventoryItem> updateInventoryItems = new ArrayList<>();
            List<AssignedOrderItem> assignedOrderItems = new ArrayList<>();
            List<ProductWarehouse> productWarehouses = new ArrayList<>();
            UUID orderId = request.getOrderId();
            // lấy hàng theo inventory_item theo thứ tự ưu tiên các lô được nhập vào kho sớm nhất sẽ được lấy trước
            for (AssignedOrderItemRequest.AssignedOrderItemRequestDetail detail : request.getItems()) {
                List<InventoryItem> inventoryItemList = inventoryItemRepository
                        .getInventoryItemByProductIdAndBayIdAndWarehouseIdOrderByCreatedStamp(
                            detail.getProductId(), detail.getBayId(), detail.getWarehouseId());
                if (inventoryItemList.isEmpty()) {
                    log.warn(String.format("Find Inventory item with detail %s not exist", detail));
                    return false;
                }

                BigDecimal totalAssignedProductQuantity = detail.getQuantity();
                BigDecimal totalUpdateProductQuantity = BigDecimal.ZERO;
                for (InventoryItem item : inventoryItemList) {
                    if (totalUpdateProductQuantity.compareTo(totalAssignedProductQuantity) < 0) {
                        // số lượng hàng cho product hiện tại chưa đủ, cần tiếp tục lấy ở inventory_item này nữa
                        BigDecimal assignedOrderItemQuantity;
                        BigDecimal newQuantity;
                        if (totalUpdateProductQuantity.add(item.getQuantityOnHandTotal()).compareTo(totalAssignedProductQuantity) < 0) {
                            // nếu lấy hết số lượng hàng ở inventory_item này mà vẫn chưa đủ số lượng theo request thì
                            // lấy hết số lượng hàng hiện có tại inventory_item này
                            totalUpdateProductQuantity = totalUpdateProductQuantity.add(item.getQuantityOnHandTotal());
                            assignedOrderItemQuantity = item.getQuantityOnHandTotal();
                            newQuantity = BigDecimal.ZERO;
                        } else {
                            // nếu lấy hết số lượng hàng ở invetory_item này vượt quá số lượng theo request thì
                            // chỉ lấy 1 phần số lượng hàng hiện có tại inventory_item
                            BigDecimal diffQuantity = totalAssignedProductQuantity.subtract(totalUpdateProductQuantity);
                            totalUpdateProductQuantity = totalAssignedProductQuantity;
                            newQuantity = item.getQuantityOnHandTotal().subtract(diffQuantity);
                            assignedOrderItemQuantity = diffQuantity;
                        }
                        item.setQuantityOnHandTotal(newQuantity);
                        log.info(String.format("Updated inventory_item -> %s", item));
                        if (assignedOrderItemQuantity.compareTo(BigDecimal.ZERO) != 0) {
                            updateInventoryItems.add(item);
                            assignedOrderItems.add(AssignedOrderItem.builder()
                                .assignedOrderItemId(UUID.randomUUID())
                                .inventoryItemId(item.getInventoryItemId())
                                .orderId(orderId)
                                .productId(detail.getProductId())
                                .quantity(assignedOrderItemQuantity)
                                .bayId(detail.getBayId())
                                .warehouseId(detail.getWarehouseId())
                                .status(AssignedOrderItemStatus.CREATED)
                                .lotId(item.getLotId()).build());
                        }
                    }
                }

                // update product warehouse quantity
                Optional<ProductWarehouse> productWarehouseOpt = productWarehouseRepository
                        .findProductWarehouseByWarehouseIdAndProductId(detail.getWarehouseId(), detail.getProductId());
                if (productWarehouseOpt.isPresent()) {
                    ProductWarehouse productWarehouse = productWarehouseOpt.get();
                    BigDecimal newQuantity = productWarehouse.getQuantityOnHand().subtract(detail.getQuantity());
                    productWarehouse.setQuantityOnHand(newQuantity);
                    productWarehouses.add(productWarehouse);
                }
            }

            inventoryItemRepository.saveAll(updateInventoryItems);
            assignedOrderItemRepository.saveAll(assignedOrderItems);
            productWarehouseRepository.saveAll(productWarehouses);
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public List<AssignedOrderItemDTO> getAllCreatedItems() {
        List<AssignedOrderItem> items = assignedOrderItemRepository.findAllByStatus(AssignedOrderItemStatus.CREATED);
        List<AssignedOrderItemDTO> response = new ArrayList<>();
        Map<UUID, String> warehouseNameMap = warehouseService.getWarehouseNameMap();
        Map<UUID, String> productNameMap = productService.getProductNameMap();
        Map<UUID, String> bayCodeMap = bayService.getBayCodeMap();
        for (AssignedOrderItem item : items) {
            AssignedOrderItemDTO dto = buildAssignedOrderItemDTO(warehouseNameMap, productNameMap, bayCodeMap, item);
            response.add(dto);
        }
        return response;
    }

    @Override
    public AssignedOrderItemDTO getById(UUID id) {
        Optional<AssignedOrderItem> assignedOrderItemOpt = assignedOrderItemRepository.findById(id);
        if (!assignedOrderItemOpt.isPresent()) {
            log.warn(String.format("Assigned order item with id %s is not exist", id));
            return null;
        }

        Map<UUID, String> warehouseNameMap = warehouseService.getWarehouseNameMap();
        Map<UUID, String> productNameMap = productService.getProductNameMap();
        Map<UUID, String> bayCodeMap = bayService.getBayCodeMap();
        return buildAssignedOrderItemDTO(warehouseNameMap, productNameMap, bayCodeMap, assignedOrderItemOpt.get());
    }

    @Override
    @Transactional
    public AssignedOrderItemDTO update(DeliveryTripDTO.DeliveryTripItemDTO request) {
        UUID assignedOrderItemId = request.getAssignOrderItemId();
        Optional<AssignedOrderItem> assignedOrderItemOpt = assignedOrderItemRepository.findById(request.getAssignOrderItemId());
        if (!assignedOrderItemOpt.isPresent()) {
            log.warn(String.format("Assigned order item with id %s is not exist", assignedOrderItemId));
            return null;
        }

        // xóa delivery_trip_item ứng với request này
        Optional<DeliveryTripItem> itemOpt = deliveryTripItemRepository.findById(request.getDeliveryTripItemId());
        if (itemOpt.isPresent()) {
            DeliveryTripItem item = itemOpt.get();
            item.setDeleted(true);
            deliveryTripItemRepository.save(item);
        }

        // cập nhật số lượng của assigned_order_item
        AssignedOrderItem assignedOrderItem = assignedOrderItemOpt.get();
        BigDecimal newQuantity = assignedOrderItem.getQuantity().add(request.getQuantity());
        assignedOrderItem.setQuantity(newQuantity);
        assignedOrderItem.setStatus(AssignedOrderItemStatus.CREATED);
        assignedOrderItemRepository.save(assignedOrderItem);

        Map<UUID, String> warehouseNameMap = warehouseService.getWarehouseNameMap();
        Map<UUID, String> productNameMap = productService.getProductNameMap();
        Map<UUID, String> bayCodeMap = bayService.getBayCodeMap();
        return buildAssignedOrderItemDTO(warehouseNameMap, productNameMap, bayCodeMap, assignedOrderItem);
    }

    private AssignedOrderItemDTO buildAssignedOrderItemDTO(Map<UUID, String> warehouseNameMap,
    Map<UUID, String> productNameMap, Map<UUID, String> bayCodeMap, AssignedOrderItem item) {
        AssignedOrderItemDTO dto = AssignedOrderItemDTO.builder()
           .assignOrderItemId(item.getAssignedOrderItemId())
           .productId(item.getProductId())
           .warehouseId(item.getWarehouseId())
           .bayId(item.getBayId())
           .lotId(item.getLotId())
           .orderId(item.getOrderId())
           .status(item.getStatus().getName())
           .quantity(item.getQuantity()).build();

        if (item.getProductId() != null) {
            dto.setProductName(productNameMap.get(item.getProductId()));
        }
        if (item.getBayId() != null) {
            dto.setBayCode(bayCodeMap.get(item.getBayId()));
        }
        if (item.getWarehouseId() != null) {
            dto.setWarehouseName(warehouseNameMap.get(item.getWarehouseId()));
        }

        if (item.getOrderId() != null) {
            Optional<SaleOrderHeader> saleOrderHeaderOpt = saleOrderHeaderRepository.findById(item.getOrderId());
            if (saleOrderHeaderOpt.isPresent()) {
                SaleOrderHeader saleOrderHeader = saleOrderHeaderOpt.get();
                if (saleOrderHeader.getCustomerAddressId() != null) {
                    Optional<CustomerAddress> addressOpt = customerAddressRepository.findById(saleOrderHeader.getCustomerAddressId());
                    if (addressOpt.isPresent()) {
                        CustomerAddress address = addressOpt.get();
                        dto.setCustomerAddressId(address.getCustomerAddressId());
                        dto.setCustomerAddressName(address.getAddressName());
                    }
                }
            }
        }
        return dto;
    }

}
