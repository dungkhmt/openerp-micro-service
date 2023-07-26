package com.hust.wmsbackend.management;

import com.hust.wmsbackend.WmsBackendApplication;
import com.hust.wmsbackend.management.entity.ProductPrice;
import com.hust.wmsbackend.management.entity.ReceiptBill;
import com.hust.wmsbackend.management.repository.ProductPriceRepository;
import org.joda.time.DateTimeComparator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = WmsBackendApplication.class)
public class DateTimeComparatorTest {

    @Autowired
    private ProductPriceRepository repository;

    @Test
    public void compareDateWithoutTime() {
        DateTimeComparator comparator = DateTimeComparator.getDateOnlyInstance();
        ProductPrice earlyPrice = repository.findById(UUID.fromString("e63402ea-333f-4ebe-824a-1daa31fe66f5")).get();
        ProductPrice lastPrice = repository.findById(UUID.fromString("4504215d-1fbf-4c1f-933d-acdd5f1ec45e")).get();
        Date now = new Date();
        System.out.println(comparator.compare(now, earlyPrice.getStartDate())); // < 0 => now is before earlyPrice
        System.out.println(comparator.compare(now, lastPrice.getStartDate()));
    }
}
