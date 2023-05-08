package com.hust.wmsbackend.management.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "product_price")
public class ProductPrice {
    @Id
    private UUID productPriceId;
    private UUID productId;
    private BigDecimal price;
    private Date startDate;
    private Date endDate;
    private String description;
}
