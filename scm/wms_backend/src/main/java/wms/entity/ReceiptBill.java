package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "scm_receipt_bill")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptBill extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;
    @Column(name = "receiving_date")
    private ZonedDateTime receivingDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facility_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private Facility facility;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private PurchaseOrder purchaseOrder;

    // https://stackoverflow.com/questions/4334970/hibernate-throws-multiplebagfetchexception-cannot-simultaneously-fetch-multipl
    @OneToMany(mappedBy = "receiptBill",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<ReceiptBillItem> receiptBillItems = new HashSet<>();
}
