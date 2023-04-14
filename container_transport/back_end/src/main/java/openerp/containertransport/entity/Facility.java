package openerp.containertransport.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "max_number_truck")
    private Integer maxNumberTruck;

    @Column(name = "max_number_trailer")
    private Integer maxNumberTrailer;

    @Column(name = "max_number_container")
    private Integer maxNumberContainer;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
