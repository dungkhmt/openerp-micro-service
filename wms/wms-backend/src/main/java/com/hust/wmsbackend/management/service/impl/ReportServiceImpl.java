package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.model.response.ReportDataPoint;
import com.hust.wmsbackend.management.model.response.RevenueProfitReportResponse;
import com.hust.wmsbackend.management.repository.DeliveryTripItemRepository;
import com.hust.wmsbackend.management.service.ReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
