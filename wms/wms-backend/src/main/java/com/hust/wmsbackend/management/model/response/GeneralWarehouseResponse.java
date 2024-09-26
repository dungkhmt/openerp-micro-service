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
public class GeneralWarehouseResponse {
    private String id;
    private String name;
    private List<GeneralShelfResponse> shelf;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GeneralShelfResponse {
        private String id;
        private String code;
    }
}
