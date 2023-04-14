package openerp.containertransport.entity;

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

    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "seq")
    private Integer seq;

    @Column(name = "action")
    private String action;

    @Column(name = "facility_id")
    private Integer facilityId;

    @Column(name = "arrival_time")
    private Long arrivalTime;

    @Column(name = "departure_time")
    private Long departureTime;

    @Column(name = "size")
    private Integer size;

    @Column(name = "truck_id")
    private Integer truckId;

    @Column(name = "status_id")
    private String statusId;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
