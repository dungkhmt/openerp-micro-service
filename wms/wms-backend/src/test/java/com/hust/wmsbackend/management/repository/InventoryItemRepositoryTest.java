package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.WmsBackendApplication;
import com.hust.wmsbackend.management.entity.InventoryItem;
import com.hust.wmsbackend.management.entity.Warehouse;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = WmsBackendApplication.class)
class InventoryItemRepositoryTest {
    @Autowired
    InventoryItemRepository inventoryItemRepository;

    @Test
    void getDeliveryTripItemByUserLoginId() {
        List<InventoryItem> items = inventoryItemRepository.findAllByWarehouseIdAndHavingProductId(
                UUID.fromString("f59b6a4d-9be0-40ce-ada1-d0ce78fe5fde"),
                UUID.fromString("fa15f3af-dcfa-4ffc-9f99-1c7b37b9c3dc"));
        System.out.println(items);
    }
}