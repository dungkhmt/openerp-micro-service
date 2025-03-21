package openerp.openerpresourceserver.mapper;

import openerp.openerpresourceserver.dto.RouteScheduleDto;
import openerp.openerpresourceserver.entity.RouteSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RouteScheduleMapper {

    RouteScheduleMapper INSTANCE = Mappers.getMapper(RouteScheduleMapper.class);

    RouteScheduleDto routeScheduleToDto(RouteSchedule routeSchedule);

    RouteSchedule dtoToRouteSchedule(RouteScheduleDto routeScheduleDto);
}