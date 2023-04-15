package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "inventory_item_detail")
public class InventoryItemDetail {
    @Id
    @Column(name = "code", nullable = false, length = 20)
    private String id;

    @Column(name = "id", nullable = false)
    private Integer id1;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_bill_code")
    private DeliveryBill deliveryBillCode;

    @Column(name = "delivery_bill_item_seq_id", length = 20)
    private String deliveryBillItemSeqId;

    @Column(name = "effect_date")
    private Instant effectDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}