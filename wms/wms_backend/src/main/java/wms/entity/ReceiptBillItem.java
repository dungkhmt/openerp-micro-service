package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "receipt_bill_item")
public class ReceiptBillItem {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_bill_code")
    private ReceiptBill receiptBillCode;

    @Column(name = "item_seq_id", length = 20)
    private String itemSeqId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_code")
    private PurchaseOrder orderCode;

    @Column(name = "order_item_seq_id", length = 20)
    private String orderItemSeqId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code")
    private Product productCode;

    @Column(name = "price_unit")
    private Double priceUnit;

    @Column(name = "effect_qty")
    private Integer effectQty;

    @Column(name = "receiving_date")
    private Instant receivingDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}