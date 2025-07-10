package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.model.AssignedOrderItemDTO;
import com.hust.wmsbackend.management.model.request.DeliveryTripItemSuggestRequest;

import java.util.List;

public interface DeliveryTripItemService {

    boolean updateItemStatus(String itemId, String newStatusCodeStr);

    boolean complete(String[] itemIds);

    boolean fail(String[] itemIds);

    List<AssignedOrderItemDTO> getDeliveryTripItemSuggest(DeliveryTripItemSuggestRequest request);

}
