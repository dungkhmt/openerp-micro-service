package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "delivery_bill_item")
public class DeliveryBillItem {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_bill_code")
    private DeliveryBill deliveryBillCode;

    @Column(name = "item_seq_id", length = 20)
    private String itemSeqId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_code")
    private SaleOrder orderCode;

    @Column(name = "order_item_seq_id", length = 20)
    private String orderItemSeqId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code")
    private Product productCode;

    @Column(name = "effect_qty")
    private Integer effectQty;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}