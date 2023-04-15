package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sale_order_item")
public class SaleOrderItem {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_code")
    private SaleOrder orderCode;

    @Column(name = "item_seq_id", length = 20)
    private String itemSeqId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code")
    private Product productCode;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price_unit")
    private Double priceUnit;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}