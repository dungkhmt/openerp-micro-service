package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.Route;
import openerp.openerpresourceserver.entity.RouteVehicle;
import openerp.openerpresourceserver.mapper.RouteVehicleMapper;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteVehicleDetailDto {

    public RouteVehicleDetailDto(RouteVehicle routeVehicle, String routeName, String routeCode, String description, Route.RouteStatus routeStatus){
        this.routeVehicleDto = RouteVehicleMapper.INSTANCE.routeVehicleToRouteVehicleDto(routeVehicle);
        this.routeName = routeName;
        this.routeCode = routeCode;
        this.description = description;
        this.status = routeStatus;
    }
    private RouteVehicleDto routeVehicleDto;
    private String routeName;
    private String routeCode;
    private String description;
    private Route.RouteStatus status;
}
