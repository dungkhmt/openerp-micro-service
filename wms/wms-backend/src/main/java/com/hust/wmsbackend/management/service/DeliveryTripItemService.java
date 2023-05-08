package com.hust.wmsbackend.management.service;

public interface DeliveryTripItemService {

    boolean updateItemStatus(String itemId, String newStatusCodeStr);

    boolean complete(String[] itemIds);

    boolean fail(String[] itemIds);

}
