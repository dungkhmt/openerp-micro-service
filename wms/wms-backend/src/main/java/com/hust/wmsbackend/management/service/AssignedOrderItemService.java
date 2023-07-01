package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.entity.AssignedOrderItem;
import com.hust.wmsbackend.management.model.AssignedOrderItemDTO;
import com.hust.wmsbackend.management.model.DeliveryTripDTO;
import com.hust.wmsbackend.management.model.request.AssignedOrderItemRequest;

import java.util.List;
import java.util.UUID;

public interface AssignedOrderItemService {

    boolean create(AssignedOrderItemRequest request);

    List<AssignedOrderItemDTO> getAllCreatedItems();

    AssignedOrderItemDTO getById(UUID id);

    AssignedOrderItemDTO update(DeliveryTripDTO.DeliveryTripItemDTO request);

    AssignedOrderItemDTO buildAssignedOrderItemDTO(AssignedOrderItem item);
}
