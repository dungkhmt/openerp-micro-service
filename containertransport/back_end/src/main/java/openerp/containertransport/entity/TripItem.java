package openerp.containertransport.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "container_transport_trip_item")
public class TripItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "uid")
    private String uid;

    private String code;

    @ManyToOne()
    @JoinColumn(name = "trip_id", referencedColumnName = "uid")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Trip trip;

    @Column(name = "seq")
    private Integer seq;

    @Column(name = "action")
    private String action;

    @ManyToOne()
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Facility facility;

    @Column(name = "arrival_time")
    private Long arrivalTime;

    @Column(name = "departure_time")
    private Long departureTime;

    @ManyToOne()
    @JoinColumn(name = "container_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Container container;

    @ManyToOne()
    @JoinColumn(name = "order_id", referencedColumnName = "uid")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Order order;

    @ManyToOne()
    @JoinColumn(name = "trailer_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Trailer trailer;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "status")
    private String status;

    @Column(name = "type")
    private String type;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "updated_at")
    private long updatedAt;
}
