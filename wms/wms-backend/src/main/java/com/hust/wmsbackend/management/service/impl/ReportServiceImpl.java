package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.model.response.ProductCategoryMonthlyData;
import com.hust.wmsbackend.management.model.response.ProductCategoryReport;
import com.hust.wmsbackend.management.model.response.ReportDataPoint;
import com.hust.wmsbackend.management.model.response.RevenueProfitReportResponse;
import com.hust.wmsbackend.management.repository.DeliveryTripItemRepository;
import com.hust.wmsbackend.management.service.ReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ReportServiceImpl implements ReportService {

    private DeliveryTripItemRepository deliveryTripItemRepository;

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
}
