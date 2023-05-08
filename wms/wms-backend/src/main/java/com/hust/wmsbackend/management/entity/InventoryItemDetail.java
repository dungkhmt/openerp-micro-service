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
@Table(name = "inventory_item_detail")
public class InventoryItemDetail {
    @Id
    private UUID inventoryItemDetailId;
    private UUID inventoryItemId;
    private BigDecimal quantityOnHandDiff;
    private Date effectiveDate;
}
