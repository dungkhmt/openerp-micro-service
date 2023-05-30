package com.hust.wmsbackend.management.model.response;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class ReportDataPoint {
    private String x; // First day of Month, example 2017- 01- 01, 2017- 02- 01
    private BigDecimal y; // revenue or profit value

    public ReportDataPoint(String x, BigDecimal y) {
        this.x = x;
        this.y = y;
    }
}
