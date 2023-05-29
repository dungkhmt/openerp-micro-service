package com.hust.wmsbackend.management.model.response;

import com.hust.wmsbackend.management.model.WarehouseWithBays;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarehouseDetailsResponse {
    private WarehouseWithBays info;
    private List<ProductWarehouseResponse.ProductWarehouseDetailResponse> items; // lưu danh sách các product có trong kho
}
