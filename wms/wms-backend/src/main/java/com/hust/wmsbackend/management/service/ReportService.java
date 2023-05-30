package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.model.response.ProductCategoryReport;
import com.hust.wmsbackend.management.model.response.RevenueProfitReportResponse;

public interface ReportService {

    RevenueProfitReportResponse genRevenueProfitReport();

    ProductCategoryReport genProductCategoryMonthlyReport();

}
