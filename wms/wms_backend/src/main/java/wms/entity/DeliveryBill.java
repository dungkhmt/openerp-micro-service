package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "delivery_bill")
public class DeliveryBill {
    @Id
    @Column(name = "code", nullable = false, length = 20)
    private String id;

    @Column(name = "id", nullable = false)
    private Integer id1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_code")
    private SaleOrder orderCode;

    @Column(name = "delivery_date")
    private Instant deliveryDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}