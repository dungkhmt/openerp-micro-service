package com.hust.wmsbackend.management.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_customer_address")
public class CustomerAddress {
    @Id
    private UUID customerAddressId;
    private String userLoginId;
    private String addressName;
    private BigDecimal longitude;
    private BigDecimal latitude;
}
