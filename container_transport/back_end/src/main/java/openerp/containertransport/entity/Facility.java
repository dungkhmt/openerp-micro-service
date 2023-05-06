package openerp.containertransport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "container_transport_facility")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "facility_code")
    private String facilityCode;

    @Column(name = "facility_name")
    private String facilityName;

    @Column(name = "facility_type")
    private String facilityType;

    private String address;

    @Column(name = "max_number_truck")
    private Integer maxNumberTruck;

    @Column(name = "max_number_trailer")
    private Integer maxNumberTrailer;

    @Column(name = "max_number_container")
    private Integer maxNumberContainer;

    @Column(name = "status")
    private String status;

    private BigDecimal longitude;  // kinh do

    private BigDecimal latitude;

    @Column(name = "processing_time")
    private Long processingTime;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "updated_at")
    private long updatedAt;
}
