package openerp.openerpresourceserver.mapper;

import openerp.openerpresourceserver.dto.RouteDto;
import openerp.openerpresourceserver.dto.RouteStopDto;
import openerp.openerpresourceserver.entity.Route;

import openerp.openerpresourceserver.entity.RouteStop;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RouteStopMapper {
    RouteStopMapper INSTANCE = Mappers.getMapper(RouteStopMapper.class);

    RouteStopDto routeStopToRouteStopDto(RouteStop routeStop);

    RouteStop routeDtoToRoute(RouteStopDto routeStopDto);
}
