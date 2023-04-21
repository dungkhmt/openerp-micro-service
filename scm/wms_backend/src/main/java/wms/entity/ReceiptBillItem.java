package wms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "receipt_bill_item")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptBillItem extends BaseEntity{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receipt_bill_code")
    @NotFound(action = NotFoundAction.IGNORE)
    private ReceiptBill receiptBill;

    @Column(name = "item_seq_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String seqId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_code")
    @NotFound(action = NotFoundAction.IGNORE)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_code")
    @NotFound(action = NotFoundAction.IGNORE)
    private ProductEntity product;

    @Column(name = "order_seq_id")
    private String orderSeqId;

    @Column(name = "effective_qty")
    private Integer effectiveQty;

    @Column(name = "receiving_date")
    private ZonedDateTime receivingDate;

    @Column(name = "price_unit")
    private Double price_unit;
}
