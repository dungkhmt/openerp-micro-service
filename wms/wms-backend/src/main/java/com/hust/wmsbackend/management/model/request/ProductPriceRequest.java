package com.hust.wmsbackend.management.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductPriceRequest {
    @NotBlank
    private String productId;
    @NotNull
    private BigDecimal price;
    private Date startDate;
    private Date endDate;
    private String description;
}
