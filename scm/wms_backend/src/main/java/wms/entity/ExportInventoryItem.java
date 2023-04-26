package wms.entity;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "export_inventory_item")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExportInventoryItem extends BaseEntity {
    @Column(name = "code")
    private String code;

    @Column(name = "quantity")
    private Integer quantity;
//
//    @Column(name = "effective_date")
//    private ZonedDateTime effectiveDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_bill_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private DeliveryBill deliveryBill;

    @Column(name = "delivery_bill_item_seq_id")
    private String deliveryBillItemSeqId;
}
