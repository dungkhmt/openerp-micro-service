package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "scm_delivery_bill")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryBill extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;
    @Column(name = "delivery_date")
    private ZonedDateTime deliveryDate;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "customer_code", referencedColumnName = "code")
//    @NotFound(action = NotFoundAction.IGNORE)
//    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private SaleOrder saleOrder;

    @OneToMany(mappedBy = "deliveryBill",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<DeliveryBillItem> deliveryBillItems = new HashSet<>();

    @OneToMany(mappedBy = "deliveryBill",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ShipmentItem> shipmentItems = new HashSet<>();

    @OneToMany(mappedBy = "deliveryBill",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ExportInventoryItem> exportInventoryItems = new HashSet<>();
}
