package openerp.openerpresourceserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteVehicleDto {
    private UUID id;

    @NotNull(message = "Route is required")
    private UUID routeId;

    private String routeCode;

    private RouteDto route;

    private String routeName;

    @NotNull(message = "Vehicle is required")
    private UUID vehicleId;

    private String vehiclePlateNumber;

    private String direction;

    private Instant createdAt;
    private Instant updatedAt;
}