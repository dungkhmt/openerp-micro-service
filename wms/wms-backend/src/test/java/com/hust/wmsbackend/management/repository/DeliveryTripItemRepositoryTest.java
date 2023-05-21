package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.WmsBackendApplication;
import com.hust.wmsbackend.management.entity.DeliveryTripItem;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = WmsBackendApplication.class)
class DeliveryTripItemRepositoryTest {

    @Autowired
    DeliveryTripItemRepository deliveryTripItemRepository;

    @Test
    void getDeliveryTripItemByUserLoginId() {
        List<DeliveryTripItem> items = deliveryTripItemRepository.getDeliveryTripItemByUserLoginId("dungpq");
        System.out.println(items);
    }
}