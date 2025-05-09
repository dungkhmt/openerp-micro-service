package com.hust.wmsbackend.management.service.service2.serviceImplement;

import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.entity.enumentity.AssignedOrderItemStatus;
import com.hust.wmsbackend.management.entity.enumentity.OrderStatus;
import com.hust.wmsbackend.management.model.AssignedOrderItemDTO;
import com.hust.wmsbackend.management.model.DeliveryTripDTO;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.repository.repo2.CustomerAddressRepository2;
import com.hust.wmsbackend.management.repository.repo2.InventoryItemRepository2;
import com.hust.wmsbackend.management.repository.repo2.ProductWarehouseRepository2;
import com.hust.wmsbackend.management.repository.repo2.SaleOrderHeaderRepository2;
import com.hust.wmsbackend.management.service.service2.AssignedOrderItemService;
import com.hust.wmsbackend.management.service.service2.ProductService;
import com.hust.wmsbackend.management.service.service2.WarehouseService;
import com.hust.wmsbackend.management.utils.DateTimeFormat;
import com.hust.wmsbackend.management.model.model2.request.AssignedOrderItemRequest2;
import com.hust.wmsbackend.management.repository.repo2.*;
import com.hust.wmsbackend.management.service.BayService;
import com.hust.wmsbackend.management.model.model2.response.AssignedOrderItemResponse;
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
public class AssignedOrderItemServiceImplVD implements AssignedOrderItemService {

    private AssignedOrderItemRepository assignedOrderItemRepository;
    private InventoryItemRepository2 inventoryItemRepository;
    private SaleOrderHeaderRepository2 saleOrderHeaderRepository;
    private CustomerAddressRepository2 customerAddressRepository;
    private DeliveryTripItemRepository deliveryTripItemRepository;
    private ProductWarehouseRepository2 productWarehouseRepository;

    private ProductService productService;
    private WarehouseService warehouseService;
    private BayService bayService;

    @Override
    @Transactional
    public AssignedOrderItemResponse create(AssignedOrderItemRequest2 request) {
        try {
            List<InventoryItem> updateInventoryItems = new ArrayList<>();
            List<AssignedOrderItem> assignedOrderItems = new ArrayList<>();
            List<ProductWarehouse> productWarehouses = new ArrayList<>(); //
            UUID orderId = request.getOrderId();

            // lấy hàng theo inventory_item theo thứ tự ưu tiên các lô được nhập vào kho sớm nhất sẽ được lấy trước
            for (AssignedOrderItemRequest2.AssignedOrderItemRequestDetail detail : request.getItems()) {
                List<InventoryItem> inventoryItemList = inventoryItemRepository
                        .getInventoryItemByProductIdAndBayIdAndWarehouseIdOrderByDatetimeReceivedHavingQuantity(
                            detail.getProductId(), detail.getBayId(), detail.getWarehouseId());

                if (inventoryItemList.isEmpty()) {
                    log.warn(String.format("Find Inventory item with detail %s not exist", detail));
                    return null;
                }
                BigDecimal totalAssignedProductQuantity = detail.getQuantity(); // lượng hàng cần lấy
                BigDecimal totalUpdateProductQuantity = BigDecimal.ZERO; // lượng hàng đã lấy được
                for (InventoryItem item : inventoryItemList) {
                    if (totalUpdateProductQuantity.compareTo(totalAssignedProductQuantity) < 0) {
                        // số lượng hàng cho product hiện tại chưa đủ, cần tiếp tục lấy ở inventory_item này nữa
                        BigDecimal assignedOrderItemQuantity; // lượng hàng sẽ lấy tại inventory_item này
                        BigDecimal newQuantity;
                        if (totalUpdateProductQuantity.add(item.getQuantityOnHandTotal()).compareTo(totalAssignedProductQuantity) < 0) {
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
                        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                            // nếu newQuantity là số âm
                            // throw Exception do có thể có hàng tại inventory_item này đã được lấy bởi thread khác
                            log.error("New quantity must not less than 0");
                            return null;
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
                                .originalQuantity(assignedOrderItemQuantity)
                                .lotId(item.getLotId()).build());
                        }
                    }
                }

                if (totalAssignedProductQuantity.compareTo(BigDecimal.ZERO) < 0) {
                    log.error(String.format("Not enough inventory item for this assignedOrderItemRequest %s", request));
                    return null;
                }

                // update product warehouse quantity
                Optional<ProductWarehouse> productWarehouseOpt = productWarehouseRepository
                        .findProductWarehouseByWarehouseIdAndProductId(detail.getWarehouseId(), detail.getProductId());
                if (productWarehouseOpt.isPresent()) {
                    ProductWarehouse productWarehouse = productWarehouseOpt.get();
                    BigDecimal newQuantity = productWarehouse.getQuantityOnHand().subtract(detail.getQuantity());

                    if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                        log.error("New quantity in product warehouse must not be less than 0");
                        return null;
                    }
                    productWarehouse.setQuantityOnHand(newQuantity);
                    productWarehouses.add(productWarehouse);
                }
            }

            if (request.isDone()) {
                Optional<SaleOrderHeader> orderOpt = saleOrderHeaderRepository.findById(orderId);
                SaleOrderHeader order = orderOpt.get();
                order.setStatus(OrderStatus.DISTRIBUTED);
                saleOrderHeaderRepository.save(order);
            }

            inventoryItemRepository.saveAll(updateInventoryItems);
            assignedOrderItemRepository.saveAll(assignedOrderItems);
            productWarehouseRepository.saveAll(productWarehouses);

            Map<UUID, String> productNameMap = productService.getProductNameMapNotInCache();
            Map<UUID, String> warehouseNameMap = warehouseService.getWarehouseNameMap();
            Map<UUID, String> bayCodeMap = bayService.getBayCodeMap();
            List<AssignedOrderItemResponse.AssignedOrderItem> assignedOrderItemList = new ArrayList<>();
            for (AssignedOrderItem assignedOrderItem : assignedOrderItems) {
                assignedOrderItemList.add(AssignedOrderItemResponse.AssignedOrderItem.builder()
                        .productId(assignedOrderItem.getProductId())
                        .warehouseId(assignedOrderItem.getWarehouseId())
                        .bayId(assignedOrderItem.getBayId())
                        .lotId(assignedOrderItem.getLotId())
                        .quantity(assignedOrderItem.getQuantity())
                        .productName(productNameMap.get(assignedOrderItem.getProductId()))
                        .bayCode(bayCodeMap.get(assignedOrderItem.getBayId()))
                        .warehouseName(warehouseNameMap.get(assignedOrderItem.getWarehouseId()))
                        .status(assignedOrderItem.getStatus().getName())
                        .createdDate(DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY, new Date()))
                        .build()
                );
            }
            return AssignedOrderItemResponse.builder()
                    .assignedOrderItemList(assignedOrderItemList)
                    .orderId(request.getOrderId())
                    .build();
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public AssignedOrderItemDTO getById(UUID id) {
        Optional<AssignedOrderItem> assignedOrderItemOpt = assignedOrderItemRepository.findById(id);
        if (!assignedOrderItemOpt.isPresent()) {
            log.warn(String.format("Assigned order item with id %s is not exist", id));
            return null;
        }

        return buildAssignedOrderItemDTO(assignedOrderItemOpt.get());
    }

    @Override
    public List<AssignedOrderItemDTO> getAllCreatedItems() {
        List<AssignedOrderItem> items = assignedOrderItemRepository.findAllByStatus(AssignedOrderItemStatus.CREATED);
        List<AssignedOrderItemDTO> response = new ArrayList<>();
        for (AssignedOrderItem item : items) {
            AssignedOrderItemDTO dto = buildAssignedOrderItemDTO(item);
            response.add(dto);
        }
        return response;
    }

    @Override
    @Transactional
    public AssignedOrderItemDTO buildAssignedOrderItemDTO(AssignedOrderItem item) {
        Map<UUID, String> warehouseNameMap = warehouseService.getWarehouseNameMapNotInCache();
        Map<UUID, String> productNameMap = productService.getProductNameMapNotInCache();
        Map<UUID, String> bayCodeMap = bayService.getBayCodeMap();
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

        return buildAssignedOrderItemDTO(assignedOrderItem);
    }

}
