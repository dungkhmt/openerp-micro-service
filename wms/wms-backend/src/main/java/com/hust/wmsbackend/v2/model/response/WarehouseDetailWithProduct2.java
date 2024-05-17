package com.hust.wmsbackend.v2.model.response;


import com.hust.wmsbackend.management.model.WarehouseWithBays;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarehouseDetailWithProduct2 {
        private String id;
        @NotBlank
        private String address;
        @NotBlank
        private String code;
        @NotBlank
        private String name;
        @Valid
        private List<BayWithProduct> bayWithProducts;

        @Data
        @AllArgsConstructor
        @Builder
        public static class BayWithProduct {
                private UUID bayId;
                @NotBlank
                private String code;
                private UUID productId;
                private BigDecimal quantityOnHandTotal;
        }
}
