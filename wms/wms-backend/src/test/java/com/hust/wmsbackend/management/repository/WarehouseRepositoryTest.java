package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.WmsBackendApplication;
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
class WarehouseRepositoryTest {
    @Autowired
    WarehouseRepository warehouseRepository;

    @Test
    void getDeliveryTripItemByUserLoginId() {
        List<Warehouse> warehouses = warehouseRepository.getWarehousesByProductId(UUID.fromString("fa15f3af-dcfa-4ffc-9f99-1c7b37b9c3dc"));
        System.out.println(warehouses);
    }
}