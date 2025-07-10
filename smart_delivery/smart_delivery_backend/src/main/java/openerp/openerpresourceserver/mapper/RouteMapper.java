package openerp.openerpresourceserver.mapper;

import openerp.openerpresourceserver.dto.RouteDto;
import openerp.openerpresourceserver.entity.Route;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RouteMapper {
    RouteMapper INSTANCE = Mappers.getMapper(RouteMapper.class);

    RouteDto routeToRouteDto(Route route);

    Route routeDtoToRoute(RouteDto routeDto);
}
