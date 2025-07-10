package com.hust.wmsbackend.management.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
@Table(name = "wms_inventory_item")
@EntityListeners(AuditingEntityListener.class)
public class InventoryItem {
    @Id
    private UUID inventoryItemId;
    private UUID productId;
    private String lotId; // id của lô hàng
    private UUID warehouseId;
    private UUID bayId;

    private BigDecimal quantityOnHandTotal;
    private BigDecimal importPrice;

    private String currencyUomId;
    private Date datetimeReceived;
    private Date expireDate;
    @LastModifiedDate
    private Date lastUpdatedStamp;
    @CreatedDate
    private Date createdStamp;

    private String description;
    private boolean isInitQuantity;
}
