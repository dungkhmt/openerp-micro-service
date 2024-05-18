package com.hust.wmsbackend.v2.service;

import com.hust.wmsbackend.management.entity.AssignedOrderItem;
import com.hust.wmsbackend.management.model.AssignedOrderItemDTO;
import com.hust.wmsbackend.management.model.DeliveryTripDTO;
import com.hust.wmsbackend.management.model.request.AssignedOrderItemRequest;
import com.hust.wmsbackend.v2.model.request.AssignedOrderItemRequest2;
import com.hust.wmsbackend.v2.model.response.AssignedOrderItemResponse;

import java.util.List;
import java.util.UUID;

public interface AssignedOrderItemService {

    AssignedOrderItemResponse create(AssignedOrderItemRequest2 request);

    List<AssignedOrderItemDTO> getAllCreatedItems();

    AssignedOrderItemDTO getById(UUID id);

    AssignedOrderItemDTO update(DeliveryTripDTO.DeliveryTripItemDTO request);

    AssignedOrderItemDTO buildAssignedOrderItemDTO(AssignedOrderItem item);
}
