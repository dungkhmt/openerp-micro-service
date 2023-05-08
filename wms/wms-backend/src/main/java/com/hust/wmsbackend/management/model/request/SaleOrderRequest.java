package com.hust.wmsbackend.management.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SaleOrderRequest {
    @NotBlank
    private String customerName;
    @NotBlank
    private String customerPhoneNumber;
    private UUID customerAddressId;
    private String addressName;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String description;
    @NotNull
    private long paymentTypeCode;
    private List<CartItemRequest.Item> items;
    @NotNull
    private long orderTypeCode;
}
