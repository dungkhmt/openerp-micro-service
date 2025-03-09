package openerp.openerpresourceserver.mapper;

import openerp.openerpresourceserver.dto.RouteVehicleDto;
import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.entity.RouteVehicle;
import openerp.openerpresourceserver.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RouteVehicleMapper {
    RouteVehicleMapper INSTANCE = Mappers.getMapper(RouteVehicleMapper.class);

    RouteVehicleDto routeVehicleToRouteVehicleDto(RouteVehicle vehicle);

    RouteVehicle routeVehicleDtoToRouteVehicle(RouteVehicleDto routeVehicleDto);
}
