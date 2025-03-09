package openerp.openerpresourceserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.Route;


import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto {
    private UUID routeId;


    @NotBlank(message = "Route code is required")
    private String routeCode;

    @NotBlank(message = "Route name is required")
    private String routeName;

    private String description;
    private Float totalDistance;
    private Integer estimatedDuration;
    private Route.RouteStatus status;
    private String notes;
    private List<RouteStopDto> stops;
}