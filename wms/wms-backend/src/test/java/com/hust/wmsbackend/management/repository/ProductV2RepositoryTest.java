package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.WmsBackendApplication;
import com.hust.wmsbackend.management.model.response.ProductDiffHistory;
import com.hust.wmsbackend.management.model.response.ProductReportResponse;
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
class ProductV2RepositoryTest {

    @Autowired
    private ProductV2Repository productV2Repository;

    @Test
    public void getProductsDataForReport() {
        List<ProductReportResponse> responseList = productV2Repository.getProductsDataForReport();
        System.out.println(responseList);
    }

    @Test
    public void getProductsDiffHistoryDataTest() {
        List<ProductV2Repository.ProductDiffHistoryInterface> data = productV2Repository.getProductsDiffHistoryData();
        for (ProductV2Repository.ProductDiffHistoryInterface d : data) {
            System.out.println(String.format("Product name: %s, product id %s, date %s", d.getProductName(), d.getProductId(), d.getEffectiveDateStr()));
        }
    }
}