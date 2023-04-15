package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "inventory_item")
public class InventoryItem {
    @Id
    @Column(name = "code", nullable = false, length = 20)
    private String id;

    @Column(name = "id", nullable = false)
    private Integer id1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_code")
    private Facility facilityCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code")
    private Product productCode;

    @Column(name = "lot_code", length = 20)
    private String lotCode;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "receiving_date")
    private Instant receivingDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}