package com.hust.wmsbackend.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
@Builder
public class WarehouseWithBays {
    private String id;
    @NotBlank
    private String address;
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    @Min(value = 0)
    private int warehouseLength;
    @Min(value = 0)
    private int warehouseWidth;
    private BigDecimal longitude;
    private BigDecimal latitude;
    @Valid
    private List<Shelf> listShelf;

    @Data
    @AllArgsConstructor
    @Builder
    public static class Shelf {
        private String id;
        @NotBlank
        private String code;
        @Min(value = 0)
        private Integer x;
        @Min(value = 0)
        private Integer y;
        @Min(value = 0)
        private Integer width;
        @Min(value = 0)
        private Integer length;
        private boolean canBeDelete;
    }
}
