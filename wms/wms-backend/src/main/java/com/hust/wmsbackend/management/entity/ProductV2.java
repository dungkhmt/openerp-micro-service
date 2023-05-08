package com.hust.wmsbackend.management.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "product")
public class ProductV2 {
    @Id
    private UUID productId;
    private String code;
    private String name;
    private String description;

    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal area;

    private String uom;
    private UUID categoryId;

    private String imageContentType;
    private Long imageSize;
    @Lob
    private byte[] imageData;
}
