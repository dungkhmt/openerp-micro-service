package com.hust.wmsbackend.management.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "wms_product")
public class Product {
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

//    @JsonCreator
//    public Product(@JsonProperty("productId") String productId, @JsonProperty("code") String code,
//                   @JsonProperty("name") String name, @JsonProperty("description") String description,
//                   @JsonProperty("height") String height, @JsonProperty("weight") String weight,
//                   @JsonProperty("area") String area, @JsonProperty("uom") String uom,
//                   @JsonProperty("categoryId") String categoryId, @JsonProperty("imageContentType") String imageContentType,
//                   @JsonProperty("imageSize") Long imageSize, @JsonProperty("imageData") String imageData) {
//        this.productId = UUID.fromString(productId);
//        this.code = code;
//        this.name = name;
//        this.description = description;
//        this.height = height;
//        this.weight = weight;
//        this.area = area;
//        this.uom = uom;
//        this.categoryId = categoryId != null ? UUID.fromString(categoryId) : null;
//        this.imageContentType = imageContentType;
//        this.imageSize = imageSize;
//        this.imageData = null; // TODO
//    }
}
