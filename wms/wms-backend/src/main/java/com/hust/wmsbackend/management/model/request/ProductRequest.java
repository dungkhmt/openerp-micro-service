package com.hust.wmsbackend.management.model.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductRequest {
    private String productId;
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private String description;

    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal area;

    private String uom;
    private String categoryId;

    private MultipartFile image;

    private List<InitProductQuantity> initProductQuantityList;

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class InitProductQuantity {
        private String warehouseId;
        private String warehouseName;
        private String bayId;
        private String code;
        private BigDecimal quantity;
        private BigDecimal importPrice;
        private BigDecimal exportPrice;
        private String lotId;
    }

}
