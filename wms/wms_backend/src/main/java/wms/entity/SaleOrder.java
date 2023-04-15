package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sale_order")
public class SaleOrder {
    @Id
    @Column(name = "code", nullable = false, length = 20)
    private String id;

    @Column(name = "id", nullable = false)
    private Integer id1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_code")
    private Customer customerCode;

    @Column(name = "status", length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bought_by")
    private Customer boughtBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserLogin createdBy;

    @Column(name = "total_money")
    private Double totalMoney;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "total_payment")
    private Double totalPayment;

    @Column(name = "order_date")
    private Instant orderDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}