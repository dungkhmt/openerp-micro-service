package com.hust.wmsbackend.management.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@Table(name = "wms_product_warehouse")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWarehouse {
    @Id
    private UUID productWarehouseId;
    private UUID productId;
    private UUID warehouseId;
    private BigDecimal quantityOnHand;
}
