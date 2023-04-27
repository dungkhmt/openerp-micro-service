package wms.entity;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "scm_shipment_item")
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentItem extends BaseEntity{
    @Column(name = "code")
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shipment_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_bill_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private DeliveryBill deliveryBill;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_trip_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private DeliveryTrip deliveryTrip;

    @Column(name = "delivery_bill_item_seq_id")
    private String deliveryBillItemSeqId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "trip_del_seq")
    private String tripSeqId;

}
