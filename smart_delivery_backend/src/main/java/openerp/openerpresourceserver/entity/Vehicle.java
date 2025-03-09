package openerp.openerpresourceserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.entity.enumentity.VehicleType;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "smartdelivery_vehicle")
public class Vehicle {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid1")
    private UUID vehicleId;
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    private String plateNumber;
    private Long volumeCapacity;
    private Long weightCapacity;
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;
    private String manufacturer;
    private String model;
    private String yearOfManufacture;
    private UUID driverId;
    private String driverCode;
    private String driverName;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;


}
