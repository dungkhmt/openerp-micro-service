package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.WmsBackendApplication;
import com.hust.wmsbackend.management.entity.DeliveryTripItem;
import com.hust.wmsbackend.management.model.response.ProductCategoryMonthlyData;
import com.hust.wmsbackend.management.model.response.ReportDataPoint;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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

    @Test
    void getRevenueDataPoints() {
        List<ReportDataPoint> dataPoints = deliveryTripItemRepository.getDataPointsForRevenue();
        System.out.println(dataPoints);
    }

    @Test
    void getExpenseDataPoints() {
        List<ReportDataPoint> dataPoints = deliveryTripItemRepository.getDataPointsForProfit();
        System.out.println(dataPoints);
    }

    @Test
    void getProductCategoryMonthlyData() {
        List<ProductCategoryMonthlyData> productCategoryMonthlyReports = deliveryTripItemRepository.getProductCategoryMonthlyData();
        System.out.println(productCategoryMonthlyReports);
    }
}