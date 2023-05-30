package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.model.response.ProductCategoryReport;
import com.hust.wmsbackend.management.model.response.ProductDiffHistory;
import com.hust.wmsbackend.management.model.response.ProductReportResponse;
import com.hust.wmsbackend.management.model.response.RevenueProfitReportResponse;

import java.util.List;
import java.util.Map;

public interface ReportService {

    RevenueProfitReportResponse genRevenueProfitReport();

    ProductCategoryReport genProductCategoryMonthlyReport();

    List<ProductReportResponse> genProductsReport();

    Map<String, List<ProductDiffHistory>> genProductDiffReport();

}
