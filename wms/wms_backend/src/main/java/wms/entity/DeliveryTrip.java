package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "delivery_trip")
public class DeliveryTrip {
    @Id
    @Column(name = "code", nullable = false, length = 20)
    private String id;

    @Column(name = "id", nullable = false)
    private Integer id1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_code")
    private Shipment shipmentCode;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}