package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scm_sale_order")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrder extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private Customer customer;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "user_login_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserRegister user;

    @Column(name = "total_money")
    private Double totalMoney;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "total_payment")
    private Double totalPayment;

    @OneToMany(mappedBy = "saleOrder",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<SaleOrderItem> saleOrderItems = new ArrayList<>();

    @OneToMany(mappedBy = "saleOrder",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<DeliveryBill> deliveryBills = new ArrayList<>();;

    @OneToMany(mappedBy = "saleOrder",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<DeliveryBillItem> deliveryBillItems = new ArrayList<>();
}
