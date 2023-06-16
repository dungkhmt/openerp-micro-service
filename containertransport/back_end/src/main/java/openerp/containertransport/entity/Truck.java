package openerp.containertransport.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "container_transport_trucks")
public class Truck implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "truck_code")
    private String truckCode;

    @ManyToOne()
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Facility facility;

    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "driver_id")
    private String driverId;

    @Column(name = "status")
    private String status;

    @Column(name = "license plates")
    private String licensePlates;

    @Column(name = "brand_truck")
    private String brandTruck;

    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "updated_at")
    private long updatedAt;
}
