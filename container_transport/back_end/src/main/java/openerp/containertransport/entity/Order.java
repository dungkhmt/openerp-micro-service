package openerp.containertransport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "container_transport_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "customer_id")
    private Integer customerId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "order_container",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "container_id"))
    private List<Container> roles = new ArrayList<>();

    @Column(name = "from_facility_id")
    private Integer fromFacilityId;

    @Column(name = "to_facility")
    private Integer toFacility;

    @Column(name = "early_delivery_time")
    private long earlyDeliveryTime;

    @Column(name = "late_delivery_time")
    private long lateDeliveryTime;

    @Column(name = "early_pickup_time")
    private long earlyPickupTime;

    @Column(name = "late_pickup_time")
    private long latePickupTime;

    @Column(name = "status_id")
    private String statusId;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
