package openerp.containertransport.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Column(name = "uid")
    private String uid;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "type")
    private String type;

    @Column(name = "weight")
    private Long weight;

    @Column(name = "is_break_romooc")
    private Boolean isBreakRomooc;

    @ManyToOne()
    @JoinColumn(name = "container_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Container container;

    @ManyToOne()
    @JoinColumn(name = "from_facility", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Facility fromFacility;

    @ManyToOne()
    @JoinColumn(name = "to_facility", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Facility toFacility;

    @Column(name = "early_delivery_time")
    private long earlyDeliveryTime;

    @Column(name = "late_delivery_time")
    private long lateDeliveryTime;

    @Column(name = "early_pickup_time")
    private long earlyPickupTime;

    @Column(name = "late_pickup_time")
    private long latePickupTime;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "updated_at")
    private long updatedAt;
}
