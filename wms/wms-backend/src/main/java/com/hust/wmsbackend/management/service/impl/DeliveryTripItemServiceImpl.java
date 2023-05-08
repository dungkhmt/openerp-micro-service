package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.DeliveryTripItem;
import com.hust.wmsbackend.management.entity.enumentity.DeliveryTripItemStatus;
import com.hust.wmsbackend.management.repository.DeliveryTripItemRepository;
import com.hust.wmsbackend.management.service.DeliveryTripItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DeliveryTripItemServiceImpl implements DeliveryTripItemService {

    private DeliveryTripItemRepository deliveryTripItemRepository;

    @Override
    public boolean updateItemStatus(String itemId, String newStatusCodeStr) {
        Optional<DeliveryTripItem> itemOpt = deliveryTripItemRepository.findById(itemId);
        if (!itemOpt.isPresent()) {
            log.warn(String.format("Delivery trip item with id %s is not exist", itemId));
            return false;
        }
        DeliveryTripItem item = itemOpt.get();
        DeliveryTripItemStatus newStatus = DeliveryTripItemStatus.findByCode(newStatusCodeStr);
        if (newStatus == null) {
            log.warn(String.format("New status for %s not found", newStatusCodeStr));
            return false;
        }
        item.setStatus(newStatus);
        deliveryTripItemRepository.save(item);
        return true;
    }

    @Override
    @Transactional
    public boolean complete(String[] itemIds) {
        List<String> itemIdList = Arrays.asList(itemIds);
        if (itemIdList.isEmpty()) {
            log.info("Item id list to update complete is empty");
            return false;
        }
        List<DeliveryTripItem> updateItems = new ArrayList<>();
        for (String itemId : itemIdList) {
            DeliveryTripItem item = getByIdOrThrow(itemId);
            if (item.getStatus() == DeliveryTripItemStatus.CREATED) {
                throw new RuntimeException("Delivery trip item is CREATED status");
            }
            item.setStatus(DeliveryTripItemStatus.DONE);
            updateItems.add(item);
        }
        deliveryTripItemRepository.saveAll(updateItems);
        return true;
    }

    @Override
    public boolean fail(String[] itemIds) {
        List<String> itemIdList = Arrays.asList(itemIds);
        if (itemIdList.isEmpty()) {
            log.info("Item id list to update comple is empty");
            return false;
        }
        List<DeliveryTripItem> updateItems = new ArrayList<>();
        for (String itemId : itemIdList) {
            DeliveryTripItem item = getByIdOrThrow(itemId);
            if (item.getStatus() == DeliveryTripItemStatus.CREATED) {
                throw new RuntimeException("Delivery trip item is CREATED status");
            }
            item.setStatus(DeliveryTripItemStatus.FAIL);
            updateItems.add(item);
        }
        deliveryTripItemRepository.saveAll(updateItems);
        return true;
    }

    public DeliveryTripItem getByIdOrThrow(String itemId) {
        Optional<DeliveryTripItem> itemOpt = deliveryTripItemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            return itemOpt.get();
        }
        throw new RuntimeException(String.format("Delivery trip item with id %s is not exist", itemId));
    }
}
