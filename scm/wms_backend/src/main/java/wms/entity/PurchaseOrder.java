package wms.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "scm_purchase_order")
@Builder
// https://stackoverflow.com/questions/40266770/spring-jpa-bi-directional-cannot-evaluate-tostring
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;

    @Column(name = "supplier_code")
    private String supplierCode;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bought_by", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private Facility facility;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "user_login_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserRegister user;

    @Column(name = "total_money")
    private Double totalMoney;

    @Column(name = "vat")
    private Double vat;

    @Column(name = "total_payment")
    private Double totalPayment;

    @OneToMany(mappedBy = "purchaseOrder",fetch = FetchType.EAGER)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();

    @OneToMany(mappedBy = "purchaseOrder",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private List<ReceiptBill> receiptBills = new ArrayList<>();;

    @OneToMany(mappedBy = "purchaseOrder",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private List<ReceiptBillItem> receiptBillItems = new ArrayList<>();
}
