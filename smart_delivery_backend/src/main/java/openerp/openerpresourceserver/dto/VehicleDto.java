package openerp.openerpresourceserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.entity.enumentity.VehicleType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto{
    private UUID vehicleId;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    private Long weightCapacity;
    private Long volumeCapacity;
    private VehicleStatus status;
    private String manufacturer;
    private String model;
    private String yearOfManufacture;
    private UUID driverId;
    private String driverCode;
    private String driverName;

}
