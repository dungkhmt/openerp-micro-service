package com.hust.wmsbackend.management.controller;

import com.hust.wmsbackend.management.model.response.ProductCategoryReport;
import com.hust.wmsbackend.management.model.response.RevenueProfitReportResponse;
import com.hust.wmsbackend.management.service.ReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wmsv2/report")
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ReportController {

    private ReportService reportService;

    @GetMapping("/revenue-profit")
    public ResponseEntity<RevenueProfitReportResponse> genRevenueProfitReport() {
        return ResponseEntity.ok(reportService.genRevenueProfitReport());
    }

    @GetMapping("/product-category-monthly")
    public ResponseEntity<ProductCategoryReport> genProductCategoryMonthlyReport() {
        return ResponseEntity.ok(reportService.genProductCategoryMonthlyReport());
    }

}
