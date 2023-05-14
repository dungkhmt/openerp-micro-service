package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.entity.enumentity.AssignedOrderItemStatus;
import com.hust.wmsbackend.management.entity.enumentity.DeliveryTripItemStatus;
import com.hust.wmsbackend.management.entity.enumentity.DeliveryTripStatus;
import com.hust.wmsbackend.management.model.DeliveryTripDTO;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.service.*;
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
public class DeliveryTripServiceImpl implements DeliveryTripService {

    private DeliveryTripRepository deliveryTripRepository;
    private DeliveryTripItemRepository deliveryTripItemRepository;
    private AssignedOrderItemRepository assignedOrderItemRepository;
    private SaleOrderHeaderRepository saleOrderHeaderRepository;
    private CustomerAddressRepository customerAddressRepository;

    private WarehouseService warehouseService;
    private BayService bayService;
    private ProductService productService;
    private DeliveryManagementService deliveryManagementService;
    private SaleOrderService saleOrderService;

    @Override
    @Transactional
    public DeliveryTripDTO create(Principal principal, DeliveryTripDTO request) {
        DeliveryTrip trip;
        if (request.getDeliveryTripId() == null) {
            trip = DeliveryTrip.builder()
                    .createdBy(principal.getName())
                    .status(DeliveryTripStatus.CREATED)
                    .shipmentId(request.getShipmentId()).build();
            DeliveryTrip t = deliveryTripRepository.save(trip);
            request.setDeliveryTripId(t.getDeliveryTripId());
            return request;
        }

        String deliveryTripId = request.getDeliveryTripId();
        Optional<DeliveryTrip> tripOpt = deliveryTripRepository.findById(request.getDeliveryTripId());
        if (!tripOpt.isPresent()) {
            String message = String.format("Delivery Trip id %s is not exist", request.getDeliveryTripId());
            log.warn(message);
            throw new RuntimeException(message);
        }

        trip = tripOpt.get();
        trip.setVehicleId(request.getVehicleId());
        trip.setDeliveryPersonId(request.getDeliveryPersonId());
        trip.setTotalLocations(request.getTotalLocations());
        trip.setWarehouseId(request.getWarehouseId());

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
                // nếu đã gán tất cả item cho chuyến giao hàng này
                // thì sẽ cập nhật status của assigned order item này thành DONE trong database
                updateItemAdder.setStatus(AssignedOrderItemStatus.DONE);
            }
            updateItemAdder.setQuantity(newQuantity);
            updateAssignedOrderItems.add(updateItemAdder);
        }
        // TODO: Calculate distance here

        deliveryTripRepository.save(trip);
        deliveryTripItemRepository.saveAll(items);
        assignedOrderItemRepository.saveAll(updateAssignedOrderItems);
        return new DeliveryTripDTO(trip);
    }

    @Override
    public List<DeliveryTripDTO> getAll() {
        List<DeliveryTrip> trips = deliveryTripRepository.findAll();
        return trips.stream().map(trip -> new DeliveryTripDTO(trip)).collect(Collectors.toList());
    }

    @Override
    public DeliveryTripDTO getById(String tripId) {
        Optional<DeliveryTrip> tripOpt = deliveryTripRepository.findById(tripId);
        if (!tripOpt.isPresent()) {
            log.warn(String.format("Trip id %s is not exist", tripId));
            return null;
        }

        Map<UUID, String> warehouseNameMap = warehouseService.getWarehouseNameMap();
        DeliveryTrip trip = tripOpt.get();
        DeliveryTripDTO response = new DeliveryTripDTO(trip);
        if (trip.getWarehouseId() != null) {
            response.setWarehouseName(warehouseNameMap.get(trip.getWarehouseId()));
        }

        Map<String, String> personNameMap = deliveryManagementService.getDeliveryPersonNameMap();
        if (trip.getDeliveryPersonId() != null) {
            response.setDeliveryPersonName(personNameMap.get(trip.getDeliveryPersonId()));
        }

        List<DeliveryTripDTO.DeliveryTripItemDTO> responseItems = new ArrayList<>();
        List<DeliveryTripItem> items = deliveryTripItemRepository.findAllByDeliveryTripIdAndIsDeleted(tripId, trip.isDeleted());
        Map<UUID, String> productNameMap = productService.getProductNameMap();
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
                                                                                         .orderId(item.getOrderId()).build();
            if (assignedOrderItem.getOrderId() != null) {
                SaleOrderHeader saleOrderHeader = saleOrderHeaderRepository.findById(assignedOrderItem.getOrderId()).get();
                CustomerAddress customerAddress = customerAddressRepository.findById(saleOrderHeader.getCustomerAddressId()).get();
                dto.setCustomerAddressName(customerAddress.getAddressName());
                dto.setCustomerName(saleOrderHeader.getCustomerName());
                dto.setCustomerPhone(saleOrderHeader.getCustomerPhoneNumber());
            }
            responseItems.add(dto);
        }
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
        DeliveryTrip trip = deliveryTripOpt.get();
        trip.setDeleted(true);

        // delete delivery item
        // re-calculate quantity of assigned_order_item
        List<DeliveryTripItem> items = deliveryTripItemRepository.findAllByDeliveryTripIdAndIsDeletedIsFalse(tripId);
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
    public DeliveryTripDTO estimateDistance(String deliveryTripId) {
        return null;
        // TODO: code
    }

    @Override
    public List<DeliveryTripDTO> getTodayDeliveryTrip(Principal principal) {
        List<DeliveryTrip> trips = deliveryTripRepository.findTodayDeliveryTripsByPerson(principal.getName(),
            Arrays.asList(DeliveryTripStatus.CREATED.getCode(), DeliveryTripStatus.DELIVERING.getCode()));
        return trips.stream().map(trip -> getById(trip.getDeliveryTripId())).collect(Collectors.toList());
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
        int numFailItems = 0;
        for (DeliveryTripItem item : items) {
            if (item.getStatus() == DeliveryTripItemStatus.FAIL) {
                numFailItems += 1;
                break;
            }
        }
        if (numFailItems > 0) {
            trip.setStatus(DeliveryTripStatus.FAIL);
        } else {
            trip.setStatus(DeliveryTripStatus.DONE);
        }
        deliveryTripRepository.save(trip);
        saleOrderService.updateStatusByDeliveryTripItem(items.stream().map(DeliveryTripItem::getOrderId).collect(Collectors.toSet()));
        return true;
    }

    @Override
    @Transactional
    public boolean startDelivery(String deliveryTripId) {
        DeliveryTrip trip = findOrThrow(deliveryTripId);
        trip.setStatus(DeliveryTripStatus.DELIVERING);
        deliveryTripRepository.save(trip);
        List<DeliveryTripItem> items = deliveryTripItemRepository.findAllByDeliveryTripIdAndIsDeleted(deliveryTripId, false);
        for (DeliveryTripItem item : items) {
            item.setStatus(DeliveryTripItemStatus.DELIVERING);
        }
        deliveryTripItemRepository.saveAll(items);
        return true;
    }

    private DeliveryTrip findOrThrow(String deliveryTripId) {
        Optional<DeliveryTrip> deliveryTripOpt = deliveryTripRepository.findById(deliveryTripId);
        if (!deliveryTripOpt.isPresent()) {
            throw new RuntimeException(String.format("Delivery trip with id %s is not exist", deliveryTripId));
        }
        return deliveryTripOpt.get();
    }
}
