package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.model.DeliveryTripDTO;
import com.hust.wmsbackend.management.model.response.DeliveryBillWithItems;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.service.BayService;
import com.hust.wmsbackend.management.service.DeliveryBillService;
import com.hust.wmsbackend.management.service.ProductService;
import com.hust.wmsbackend.management.service.WarehouseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DeliveryBillServiceImpl implements DeliveryBillService {

    private DeliveryBillRepository deliveryBillRepository;
    private DeliveryTripItemRepository deliveryTripItemRepository;
    private AssignedOrderItemRepository assignedOrderItemRepository;
    private SaleOrderHeaderRepository saleOrderHeaderRepository;
    private CustomerAddressRepository customerAddressRepository;

    private ProductService productService;
    private WarehouseService warehouseService;
    private BayService bayService;

    @Override
    public List<DeliveryBillWithItems> getAll() {
        List<DeliveryBill> bills = deliveryBillRepository.findAll();
        return bills.stream().map(DeliveryBillWithItems::new).collect(Collectors.toList());
    }

    @Override
    public DeliveryBillWithItems getByDeliveryBillId(String deliveryBillId) {
        Optional<DeliveryBill> billOpt = deliveryBillRepository.findById(deliveryBillId);
        if (!billOpt.isPresent()) {
            log.warn(String.format("Delivery bill id %s is not exist", deliveryBillId));
            return null;
        }
        DeliveryBill bill = billOpt.get();
        DeliveryBillWithItems response = new DeliveryBillWithItems(bill);
        // set response item for delivery bill
        List<DeliveryTripDTO.DeliveryTripItemDTO> responseItems = new ArrayList<>();
        List<DeliveryTripItem> items = deliveryTripItemRepository.findAllByDeliveryTripId(bill.getDeliveryTripId());
        Map<UUID, String> productNameMap = productService.getProductNameMap();
        Map<UUID, String> warehouseNameMap = warehouseService.getWarehouseNameMap();
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
        response.setItems(responseItems);
        return response;
    }
}
