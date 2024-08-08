package com.hust.wmsbackend.management.service.service2.serviceImplement;

import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.entity.enumentity.AssignedOrderItemStatus;
import com.hust.wmsbackend.management.entity.enumentity.DeliveryTripItemStatus;
import com.hust.wmsbackend.management.entity.enumentity.DeliveryTripStatus;
import com.hust.wmsbackend.management.model.DeliveryTripDTO;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.service.*;
import com.hust.wmsbackend.management.service.service2.DeliveryTripService;
import com.hust.wmsbackend.management.repository.repo2.CustomerAddressRepository2;
import com.hust.wmsbackend.management.repository.repo2.SaleOrderHeaderRepository2;
import com.hust.wmsbackend.management.repository.repo2.DeliveryTripItemRepository2;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DeliveryTripServiceImplVD implements DeliveryTripService {

    private DeliveryTripRepository deliveryTripRepository;
    private DeliveryTripItemRepository2 deliveryTripItemRepository;
    private AssignedOrderItemRepository assignedOrderItemRepository;
    private SaleOrderHeaderRepository2 saleOrderHeaderRepository;
    private CustomerAddressRepository2 customerAddressRepository;
    private DeliveryBillRepository deliveryBillRepository;

    private com.hust.wmsbackend.management.service.service2.WarehouseService warehouseService;
    private BayService bayService;
    private com.hust.wmsbackend.management.service.service2.ProductService productService;
    private com.hust.wmsbackend.management.service.service2.DeliveryManagementService deliveryManagementService;
    private com.hust.wmsbackend.management.service.service2.SaleOrderService saleOrderService;

    @Override
    @Transactional
    public DeliveryTripDTO create(Principal principal, DeliveryTripDTO request) {
        DeliveryTrip trip;

        // Khởi tạo lần đầu
        if (request.getDeliveryTripId() == null) {
            trip = DeliveryTrip.builder()
                    .createdBy(principal.getName())
                    .status(DeliveryTripStatus.CREATED)
                    .shipmentId(request.getShipmentId()).build();
            DeliveryTrip t = deliveryTripRepository.save(trip);
            request.setDeliveryTripId(t.getDeliveryTripId());
            return request;
        }

        // Cập nhật
        // Get trip
        String deliveryTripId = request.getDeliveryTripId();
        Optional<DeliveryTrip> tripOpt = deliveryTripRepository.findById(deliveryTripId);
        if (!tripOpt.isPresent()) {
            String message = String.format("Delivery Trip id %s is not exist", deliveryTripId);
            log.warn(message);
            throw new RuntimeException(message);
        }
        trip = tripOpt.get();

        // Bắt đầu cập nhật
        trip.setVehicleId(request.getVehicleId());
        trip.setDeliveryPersonId(request.getDeliveryPersonId());
        trip.setTotalLocations(request.getTotalLocations());
        trip.setWarehouseId(request.getWarehouseId());
        trip.setDescription(request.getDescription());

        List<DeliveryTripItem> items = new ArrayList<>();
        List<AssignedOrderItem> updateAssignedOrderItems = new ArrayList<>();
        for (DeliveryTripDTO.DeliveryTripItemDTO item : request.getItems()) {
            DeliveryTripItem adder;
            if (item.getDeliveryTripItemId() != null) {
                // update quantity of item only
                adder = deliveryTripItemRepository.findByDeliveryTripItemIdAndIsDeletedIsFalse(item.getDeliveryTripItemId()).get();
                adder.setQuantity(item.getQuantity());
                items.add(adder);
                continue;
            }
            adder = DeliveryTripItem.builder()
                    .deliveryTripId(deliveryTripId)
                    .sequence(item.getSequence())
                    .assignedOrderItemId(item.getAssignOrderItemId())
                    .quantity(item.getQuantity())
                    .orderId(item.getOrderId())
                    .status(DeliveryTripItemStatus.CREATED)
                    .build();
            items.add(adder);

            // update quantity of assigned order items
            Optional<AssignedOrderItem> updateItemAdderOpt = assignedOrderItemRepository.findById(item.getAssignOrderItemId());
            if (!updateItemAdderOpt.isPresent()) {
                throw new RuntimeException(String.format("Assigned order item with id %s is not exist", item.getAssignOrderItemId()));
            }
            AssignedOrderItem updateItemAdder = updateItemAdderOpt.get();
            BigDecimal newQuantity = updateItemAdder.getQuantity().subtract(item.getQuantity());
            if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Quantity of product < 0 ");
            }
            if (newQuantity.compareTo(BigDecimal.ZERO) == 0) {
                // update status if done
                updateItemAdder.setStatus(AssignedOrderItemStatus.DONE);
            }
            updateItemAdder.setQuantity(newQuantity);
            updateAssignedOrderItems.add(updateItemAdder);
        }

        deliveryTripRepository.save(trip);
        deliveryTripItemRepository.saveAll(items);
        assignedOrderItemRepository.saveAll(updateAssignedOrderItems);

        List<DeliveryTripDTO.DeliveryTripItemDTO> responseItems = new ArrayList<>();
        List<DeliveryTripItem> itemsNonConvert = deliveryTripItemRepository.findAllByDeliveryTripIdAndIsDeletedIsFalse(trip.getDeliveryTripId());
        Map<UUID, String> warehouseNameMap = warehouseService.getWarehouseNameMapNotInCache();
        Map<UUID, String> productNameMap = productService.getProductNameMapNotInCache();
        Map<UUID, String> bayCodeMap = bayService.getBayCodeMap();
        for (DeliveryTripItem item : items) {
            AssignedOrderItem assignedOrderItem = assignedOrderItemRepository.findById(item.getAssignedOrderItemId()).get();
            DeliveryTripDTO.DeliveryTripItemDTO dto = DeliveryTripDTO.DeliveryTripItemDTO.builder()
                    .assignOrderItemId(item.getAssignedOrderItemId())
                    .productId(assignedOrderItem.getProductId())
                    .productName(productNameMap.get(assignedOrderItem.getProductId()))
                    .bayId(assignedOrderItem.getBayId())
                    .bayCode(bayCodeMap.get(assignedOrderItem.getBayId()))
                    .warehouseId(assignedOrderItem.getWarehouseId())
                    .warehouseName(warehouseNameMap.get(assignedOrderItem.getWarehouseId()))
                    .quantity(item.getQuantity())
                    .sequence(item.getSequence())
                    .lotId(assignedOrderItem.getLotId())
                    .deliveryTripItemId(item.getDeliveryTripItemId())
                    .statusCode(item.getStatus() != null ? item.getStatus().getName() : null)
                    .orderId(item.getOrderId())
                    .build();
            if (assignedOrderItem.getOrderId() != null) {
                SaleOrderHeader saleOrderHeader = saleOrderHeaderRepository.findById(assignedOrderItem.getOrderId()).get();
                CustomerAddress customerAddress = customerAddressRepository.findById(saleOrderHeader.getCustomerAddressId()).get();
                dto.setCustomerAddressName(customerAddress.getAddressName());
                dto.setCustomerName(saleOrderHeader.getCustomerName());
                dto.setCustomerPhone(saleOrderHeader.getCustomerPhoneNumber());
            }
            responseItems.add(dto);
        }
        DeliveryTripDTO res = new DeliveryTripDTO(trip);
        res.setItems(responseItems);
        return res;


    }


    @Override
    public List<DeliveryTripDTO> getAll() {
        List<DeliveryTrip> trips = deliveryTripRepository.findAllByIsDeletedIsFalseOrderByCreatedStampDesc();
        return trips.stream().map(DeliveryTripDTO::new).collect(Collectors.toList());
    }

    @Override
    public DeliveryTripDTO getById(String tripId) {
        Optional<DeliveryTrip> tripOpt = deliveryTripRepository.findById(tripId);
        if (!tripOpt.isPresent()) {
            log.warn(String.format("Trip id %s is not exist", tripId));
            return null;
        }

        DeliveryTrip trip = tripOpt.get();
        DeliveryTripDTO response = new DeliveryTripDTO(trip);

        Map<UUID, String> warehouseNameMap = warehouseService.getWarehouseNameMapNotInCache();
        if (trip.getWarehouseId() != null) {
            response.setWarehouseName(warehouseNameMap.get(trip.getWarehouseId()));
        }

        if (trip.getDeliveryPersonId() != null) {
            response.setDeliveryPersonName(deliveryManagementService.getNamebyId(trip.getDeliveryPersonId()));
        }

        List<DeliveryTripDTO.DeliveryTripItemDTO> responseItems = new ArrayList<>();
        List<DeliveryTripItem> items = deliveryTripItemRepository.findAllByDeliveryTripIdAndIsDeleted(tripId, trip.isDeleted());
        Map<UUID, String> productNameMap = productService.getProductNameMapNotInCache();
        Map<UUID, String> bayCodeMap = bayService.getBayCodeMap();
        for (DeliveryTripItem item : items) {
            AssignedOrderItem assignedOrderItem = assignedOrderItemRepository.findById(item.getAssignedOrderItemId()).get();
            DeliveryTripDTO.DeliveryTripItemDTO dto = DeliveryTripDTO.DeliveryTripItemDTO.builder()
                .assignOrderItemId(item.getAssignedOrderItemId())
                .productId(assignedOrderItem.getProductId())
                .productName(productNameMap.get(assignedOrderItem.getProductId()))
                .bayId(assignedOrderItem.getBayId())
                .bayCode(bayCodeMap.get(assignedOrderItem.getBayId()))
                .warehouseId(assignedOrderItem.getWarehouseId())
                .warehouseName(warehouseNameMap.get(assignedOrderItem.getWarehouseId()))
                .quantity(item.getQuantity())
                .sequence(item.getSequence())
                .lotId(assignedOrderItem.getLotId())
                .deliveryTripItemId(item.getDeliveryTripItemId())
                .statusCode(item.getStatus() != null ? item.getStatus().getName() : null)
                .orderId(item.getOrderId())
                .build();
            if (assignedOrderItem.getOrderId() != null) {
                SaleOrderHeader saleOrderHeader = saleOrderHeaderRepository.findById(assignedOrderItem.getOrderId()).get();
                CustomerAddress customerAddress = customerAddressRepository.findById(saleOrderHeader.getCustomerAddressId()).get();
                dto.setCustomerAddressName(customerAddress.getAddressName());
                dto.setCustomerName(saleOrderHeader.getCustomerName());
                dto.setCustomerPhone(saleOrderHeader.getCustomerPhoneNumber());
            }
            responseItems.add(dto);
        }
        responseItems.sort(new Comparator<DeliveryTripDTO.DeliveryTripItemDTO>() {
            @Override
            public int compare(DeliveryTripDTO.DeliveryTripItemDTO o1, DeliveryTripDTO.DeliveryTripItemDTO o2) {
                return Integer.compare(o1.getSequence(), o2.getSequence());
            }
        });
        response.setItems(responseItems);
        return response;
    }

    @Override
    @Transactional
    public DeliveryTripDTO deleteById(String tripId) {
        Optional<DeliveryTrip> deliveryTripOpt = deliveryTripRepository.findById(tripId);
        if (!deliveryTripOpt.isPresent()) {
            log.warn(String.format("Delivery trip with id %s is not exist", tripId));
            return null;
        }

        // Lấy danh sách DeliveryTripItem
        List<DeliveryTripItem> items = deliveryTripItemRepository.findAllByDeliveryTripIdAndIsDeletedIsFalse(tripId);

        // Kiểm tra trạng thái của từng DeliveryTripItem
        for (DeliveryTripItem item : items) {
            if (!item.getStatus().equals(DeliveryTripItemStatus.CREATED)) {
                log.warn(String.format("Delivery trip item with id %s has a status other than CREATED", item.getDeliveryTripItemId()));
                return null;
            }
        }

        // Tiếp tục thực hiện nếu tất cả các item đều có trạng thái CREATED
        DeliveryTrip trip = deliveryTripOpt.get();
        trip.setDeleted(true);

        List<AssignedOrderItem> assignedOrderItems = new ArrayList<>();
        for (DeliveryTripItem item : items) {
            item.setDeleted(true);
            Optional<AssignedOrderItem> assignedOrderItemOpt = assignedOrderItemRepository.findById(item.getAssignedOrderItemId());
            if (assignedOrderItemOpt.isPresent()) {
                AssignedOrderItem updateAssignedOrderItem = assignedOrderItemOpt.get();
                BigDecimal newQuantity = updateAssignedOrderItem.getQuantity().add(item.getQuantity());
                updateAssignedOrderItem.setQuantity(newQuantity);
                updateAssignedOrderItem.setStatus(AssignedOrderItemStatus.CREATED);

                assignedOrderItems.add(updateAssignedOrderItem);
            }
        }

        assignedOrderItemRepository.saveAll(assignedOrderItems);
        deliveryTripRepository.save(trip);
        deliveryTripItemRepository.saveAll(items);
        return new DeliveryTripDTO(trip);
    }


    @Override
    public List<DeliveryTripDTO> getTodayDeliveryTrip(Principal principal) {
        List<DeliveryTrip> trips = deliveryTripRepository.findTodayDeliveryTripsByPerson(principal.getName(),
            Arrays.asList(DeliveryTripStatus.CREATED.getCode(), DeliveryTripStatus.DELIVERING.getCode()));
        return trips.stream().map(trip -> new DeliveryTripDTO(trip)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean complete(String deliveryTripId) {
        DeliveryTrip trip = findOrThrow(deliveryTripId);
        if (trip.getStatus() != DeliveryTripStatus.DELIVERING) {
            throw new RuntimeException("Delivery trip current status is not DELIVERING");
        }
        List<DeliveryTripItem> items = deliveryTripItemRepository.findAllByDeliveryTripIdAndIsDeleted(deliveryTripId, false);
        for (DeliveryTripItem item : items) {
            if (item.getStatus() == DeliveryTripItemStatus.DELIVERING) {
                throw new RuntimeException("Có sản phẩm chưa được giao");
            }
        }
        List<AssignedOrderItem> updateAssignedOrderItems = new ArrayList<>();
        for (DeliveryTripItem item : items) {
            if (item.getStatus() == DeliveryTripItemStatus.FAIL) {

                // Logic mới sau khi thảo luận với thầy
                // Các delivery_trip_item fail sẽ cập nhật lại quantity của assigned_order_item tương ứng
                // KHÔNG cập nhật trực tiếp vào inventory_item và product_warehouse
                UUID assignedOrderItemId = item.getAssignedOrderItemId();
                Optional<AssignedOrderItem> assignedOrderItemOpt = assignedOrderItemRepository.findById(assignedOrderItemId);
                if (!assignedOrderItemOpt.isPresent()) {
                    throw new RuntimeException(String.format("Assigned order item with id %s is not exist", assignedOrderItemId));
                }
                AssignedOrderItem assignedOrderItem = assignedOrderItemOpt.get();
                BigDecimal newQuantity = assignedOrderItem.getQuantity().add(item.getQuantity());
                assignedOrderItem.setQuantity(newQuantity);
                assignedOrderItem.setStatus(AssignedOrderItemStatus.CREATED);
                updateAssignedOrderItems.add(assignedOrderItem);
            }
        }
        trip.setStatus(DeliveryTripStatus.DONE);
        deliveryTripRepository.save(trip);
        saleOrderService.updateStatusByDeliveryTripItem(items.stream().map(DeliveryTripItem::getOrderId).collect(Collectors.toSet()));
        assignedOrderItemRepository.saveAll(updateAssignedOrderItems);
//        inventoryItemRepository.saveAll(updateInventoryItems);
//        productWarehouseRepository.saveAll(updateProductWarehouses);
        return true;
    }

    @Override
    @Transactional
    public boolean startDelivery(String deliveryTripId, Principal principal) {
        DeliveryTrip trip = findOrThrow(deliveryTripId);
        trip.setStatus(DeliveryTripStatus.DELIVERING);
        deliveryTripRepository.save(trip);
        List<DeliveryTripItem> items = deliveryTripItemRepository.findAllByDeliveryTripIdAndIsDeleted(deliveryTripId, false);
        for (DeliveryTripItem item : items) {
            item.setStatus(DeliveryTripItemStatus.DELIVERING);
        }
        deliveryTripItemRepository.saveAll(items);
        // create delivery bill
        DeliveryBill deliveryBill = DeliveryBill.builder()
            .deliveryTripId(deliveryTripId)
            .createdBy(principal.getName())
            .build();
        deliveryBillRepository.save(deliveryBill);
        return true;
    }

    @Override
    public DeliveryTripDTO deleteFromShipmentById(String tripId) {
        Optional<DeliveryTrip> deliveryTripOpt = deliveryTripRepository.findById(tripId);
        if (!deliveryTripOpt.isPresent()) {
            log.warn(String.format("Delivery trip with id %s is not exist", tripId));
            return null;
        }
        DeliveryTrip trip = deliveryTripOpt.get();
        trip.setShipmentId(null);
        deliveryTripRepository.save(trip);
        return new DeliveryTripDTO(trip);
    }

    private DeliveryTrip findOrThrow(String deliveryTripId) {
        Optional<DeliveryTrip> deliveryTripOpt = deliveryTripRepository.findById(deliveryTripId);
        if (!deliveryTripOpt.isPresent()) {
            throw new RuntimeException(String.format("Delivery trip with id %s is not exist", deliveryTripId));
        }
        return deliveryTripOpt.get();
    }

}
