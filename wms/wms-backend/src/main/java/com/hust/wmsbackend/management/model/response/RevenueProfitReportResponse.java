package com.hust.wmsbackend.management.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueProfitReportResponse {

    private List<ReportDataPoint> revenue;
    private List<ReportDataPoint> profit;

}
