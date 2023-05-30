package com.hust.wmsbackend.management.model.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductCategoryReport {

    Map<String, List<ProductCategoryMonthlyData>> points;
    List<String> months;

}
