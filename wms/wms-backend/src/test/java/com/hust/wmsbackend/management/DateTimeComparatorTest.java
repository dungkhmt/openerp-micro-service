package com.hust.wmsbackend.management;

import com.hust.wmsbackend.WmsBackendApplication;
import com.hust.wmsbackend.management.entity.ProductPrice;
import com.hust.wmsbackend.management.entity.ReceiptBill;
import com.hust.wmsbackend.management.repository.ProductPriceRepository;
import com.hust.wmsbackend.management.utils.DateUtils;
import org.joda.time.DateTimeComparator;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Test
    public void overlapTest() throws ParseException {
        Date s1t0 = new SimpleDateFormat("dd/MM/yyyy").parse("01/05/2023");
        Date e1t0 = new SimpleDateFormat("dd/MM/yyyy").parse("30/05/2023");
        Date s2t0 = new SimpleDateFormat("dd/MM/yyyy").parse("05/05/2023");
        Date e2t0 = new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2023");
        Assert.assertEquals(DateUtils.isOverlap(s1t0, e1t0, s2t0, e2t0), true);

        Date s1t1 = new SimpleDateFormat("dd/MM/yyyy").parse("27/07/2023");
        Date e1t1 = new SimpleDateFormat("dd/MM/yyyy").parse("27/08/2023");
        Date s2t1 = new SimpleDateFormat("dd/MM/yyyy").parse("28/07/2023");
        Date e2t1 = new SimpleDateFormat("dd/MM/yyyy").parse("28/08/2023");
        Assert.assertEquals(DateUtils.isOverlap(s1t1, e1t1, s2t1, e2t1), true);

        Date s1t2 = new SimpleDateFormat("dd/MM/yyyy").parse("27/07/2023");
        Date e1t2 = new SimpleDateFormat("dd/MM/yyyy").parse("27/08/2023");
        Date s2t2 = new SimpleDateFormat("dd/MM/yyyy").parse("24/07/2023");
        Date e2t2 = new SimpleDateFormat("dd/MM/yyyy").parse("28/07/2023");
        Assert.assertEquals(DateUtils.isOverlap(s1t2, e1t2, s2t2, e2t2), true);

        Date s1t3 = new SimpleDateFormat("dd/MM/yyyy").parse("27/07/2023");
        Date e1t3 = new SimpleDateFormat("dd/MM/yyyy").parse("27/08/2023");
        Date s2t3 = new SimpleDateFormat("dd/MM/yyyy").parse("28/07/2023");
        Date e2t3 = new SimpleDateFormat("dd/MM/yyyy").parse("01/08/2023");
        Assert.assertEquals(DateUtils.isOverlap(s1t3, e1t3, s2t3, e2t3), true);

        Date s1t4 = new SimpleDateFormat("dd/MM/yyyy").parse("27/07/2023");
        Date e1t4 = new SimpleDateFormat("dd/MM/yyyy").parse("27/08/2023");
        Date s2t4 = new SimpleDateFormat("dd/MM/yyyy").parse("25/07/2023");
        Date e2t4 = new SimpleDateFormat("dd/MM/yyyy").parse("28/08/2023");
        Assert.assertEquals(DateUtils.isOverlap(s1t4, e1t4, s2t4, e2t4), true);

        Date s1t5 = new SimpleDateFormat("dd/MM/yyyy").parse("27/07/2023");
        Date e1t5 = null;
        Date s2t5 = new SimpleDateFormat("dd/MM/yyyy").parse("28/07/2023");
        Date e2t5 = null;
        Assert.assertEquals(DateUtils.isOverlap(s1t5, e1t5, s2t5, e2t5), true);

        Date s1t6 = new SimpleDateFormat("dd/MM/yyyy").parse("27/07/2023");
        Date e1t6 = null;
        Date s2t6 = new SimpleDateFormat("dd/MM/yyyy").parse("25/07/2023");
        Date e2t6 = new SimpleDateFormat("dd/MM/yyyy").parse("27/07/2023");
        Assert.assertEquals(DateUtils.isOverlap(s1t6, e1t6, s2t6, e2t6), true);

        Date s1t7 = new SimpleDateFormat("dd/MM/yyyy").parse("27/07/2023");
        Date e1t7 = new SimpleDateFormat("dd/MM/yyyy").parse("27/08/2023");
        Date s2t7 = new SimpleDateFormat("dd/MM/yyyy").parse("25/07/2023");
        Date e2t7 = null;
        Assert.assertEquals(DateUtils.isOverlap(s1t7, e1t7, s2t7, e2t7), true);
    }
}
