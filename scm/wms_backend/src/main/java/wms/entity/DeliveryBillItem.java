package wms.entity;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name = "scm_delivery_bill_item")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryBillItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "delivery_bill_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private DeliveryBill deliveryBill;

    @Column(name = "item_seq_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String seqId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private SaleOrder saleOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private ProductEntity product;

    @Column(name = "order_seq_id")
    private String orderSeqId;

    @Column(name = "effective_qty")
    private Integer effectiveQty;

//    @Column(name = "receiving_date")
//    private ZonedDateTime receivingDate;

    @Column(name = "price_unit")
    private Double price_unit;
}
