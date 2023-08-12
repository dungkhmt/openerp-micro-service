package com.hust.wmsbackend.management.model.response;

import com.hust.wmsbackend.management.entity.Warehouse;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseGeneral {
    private UUID warehouseId;
    private String name;
    private String code;
    private Integer width;
    private Integer length;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private boolean canBeDelete;

    public WarehouseGeneral(Warehouse warehouse, boolean canBeDelete) {
        this.warehouseId = warehouse == null ? null : warehouse.getWarehouseId();
        this.name = warehouse == null ? null : warehouse.getName();
        this.code = warehouse == null ? null : warehouse.getCode();
        this.width = warehouse == null ? null : warehouse.getWidth();
        this.length = warehouse == null ? null : warehouse.getLength();
        this.address = warehouse == null ? null : warehouse.getAddress();
        this.longitude = warehouse == null ? null : warehouse.getLongitude();
        this.latitude = warehouse == null ? null : warehouse.getLatitude();
        this.canBeDelete = canBeDelete;
    }
}
