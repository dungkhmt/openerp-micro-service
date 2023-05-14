package wms.entity;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name = "scm_purchase_order_item")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private PurchaseOrder purchaseOrder;

    @Column(name = "item_seq_id")
    private String seqId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private ProductEntity product;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price_unit")
    private Double priceUnit;
}
