package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.model.response.*;
import com.hust.wmsbackend.management.repository.DeliveryTripItemRepository;
import com.hust.wmsbackend.management.repository.ProductV2Repository;
import com.hust.wmsbackend.management.service.ProductService;
import com.hust.wmsbackend.management.service.ReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ReportServiceImpl implements ReportService {

    private DeliveryTripItemRepository deliveryTripItemRepository;
    private ProductV2Repository productRepository;

    private ProductService productService;

    @Override
    public RevenueProfitReportResponse genRevenueProfitReport() {
        List<ReportDataPoint> revenueDataPoints = deliveryTripItemRepository.getDataPointsForRevenue();
        List<ReportDataPoint> profitDataPoints = deliveryTripItemRepository.getDataPointsForProfit();
        return RevenueProfitReportResponse.builder()
                .profit(profitDataPoints)
                .revenue(revenueDataPoints)
                .build();
    }

    @Override
    public ProductCategoryReport genProductCategoryMonthlyReport() {
        List<ProductCategoryMonthlyData> productCategoryMonthlyData = deliveryTripItemRepository.getProductCategoryMonthlyData();
        Map<String, List<ProductCategoryMonthlyData>> points = new HashMap<>();
        List<String> months = new ArrayList<>();
        for (ProductCategoryMonthlyData data : productCategoryMonthlyData) {
            String key = data.getTime();
            List<ProductCategoryMonthlyData> categories;
            if (points.containsKey(key)) {
                categories = points.get(key);
            } else {
                categories = new ArrayList<>();
                months.add(key);
            }
            categories.add(data);
            points.put(key, categories);
        }
        for (Map.Entry<String, List<ProductCategoryMonthlyData>> entry : points.entrySet()) {
            List<ProductCategoryMonthlyData> categories = entry.getValue();
            BigDecimal totalProfit = categories.stream()
                    .map(ProductCategoryMonthlyData::getProfit)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            for (ProductCategoryMonthlyData category : categories) {
                category.setY(category.getProfit().divide(totalProfit, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            }
        }
        return ProductCategoryReport.builder().points(points).months(months).build();
    }

    @Override
    public List<ProductReportResponse> genProductsReport() {
        List<ProductReportResponse> response = productRepository.getProductsDataForReport();
        for (ProductReportResponse r : response) {
            UUID productId = UUID.fromString(r.getProductId());
            BigDecimal price = productService.getCurrPriceByProductId(productId);
            r.setTotalExportPrice(r.getTotalQuantity().multiply(price));
            r.setPrice(price);
        }
        return response;
    }

    @Override
    public Map<String, List<ProductDiffHistory>>  genProductDiffReport() {
        List<ProductV2Repository.ProductDiffHistoryInterface> historyInterfaces = productRepository.getProductsDiffHistoryData();
        Map<String, List<ProductDiffHistory>> response = new HashMap<>();
        for (ProductV2Repository.ProductDiffHistoryInterface history : historyInterfaces) {
            String productId = history.getProductId();
            List<ProductDiffHistory> histories;
            if (response.containsKey(productId)) {
                histories = response.get(productId);
            } else {
                histories = new ArrayList<>();
            }
            histories.add(new ProductDiffHistory(history.getProductId(), history.getProductName(), history.getQuantity(),
                    history.getEffectiveDateStr(), history.getType()));
            if (productId.equals("8ccfe2ba-2b6a-4029-a29c-58b363951481")) {
                System.out.println(history.getType());
            }
            response.put(productId, histories);
        }
        return response;
    }
}
