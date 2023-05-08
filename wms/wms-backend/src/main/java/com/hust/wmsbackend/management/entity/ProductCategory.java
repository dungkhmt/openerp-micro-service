package com.hust.wmsbackend.management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity(name = "product_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory {
    @Id
    private UUID categoryId;
    private String name;
}
