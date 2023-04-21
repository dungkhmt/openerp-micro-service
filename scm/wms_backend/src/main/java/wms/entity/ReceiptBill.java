package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "inventory_item")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptBill extends BaseEntity{
    @Column(name = "code")
    private String code;
    @Column(name = "receiving_date")
    private ZonedDateTime receivingDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facility_code")
    @NotFound(action = NotFoundAction.IGNORE)
    private Facility facility;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_code")
    @NotFound(action = NotFoundAction.IGNORE)
    private PurchaseOrder purchaseOrder;

    @OneToMany(mappedBy = "receiptBill",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<ReceiptBillItem> receiptBillItems;
}
