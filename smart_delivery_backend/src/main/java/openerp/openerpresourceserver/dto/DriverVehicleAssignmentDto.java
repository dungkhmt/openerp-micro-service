package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.entity.enumentity.VehicleType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverVehicleAssignmentDto {
    private UUID vehicleId;
    private String plateNumber;
    private VehicleType vehicleType;
    private String model;
    private String manufacturer;
    private VehicleStatus status;

    private UUID driverId;
    private String driverName;
    private String driverCode;
    private String driverPhone;
}