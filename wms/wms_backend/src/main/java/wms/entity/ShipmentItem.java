package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "shipment_item")
public class ShipmentItem {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_code")
    private Shipment shipmentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_bill_code")
    private DeliveryBill deliveryBillCode;

    @Column(name = "delivery_bill_item_seq_id", length = 20)
    private String deliveryBillItemSeqId;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_trip_code")
    private DeliveryTrip deliveryTripCode;

    @Column(name = "trip_delivery_seq")
    private Integer tripDeliverySeq;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}