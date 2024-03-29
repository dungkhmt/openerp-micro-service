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

    @Column(name = "uid")
    private String uid;

    @Column(name = "facility_code")
    private String facilityCode;

    @Column(name = "facility_name")
    private String facilityName;

    @Column(name = "facility_type")
    private String facilityType;

    @Column(name = "type_owner")
    private String typeOwner;

    private String address;

    private String owner;

    private BigDecimal acreage; // dien tich

    @Column(name = "number_truck")
    private Integer numberTruck;

    @Column(name = "number_trailer")
    private Integer numberTrailer;

    @Column(name = "number_container")
    private Integer numberContainer;

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

    @Column(name = "processing_time_pickup")
    private Long processingTimePickUp;

    @Column(name = "processing_time_drop")
    private Long processingTimeDrop;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "updated_at")
    private long updatedAt;
}
