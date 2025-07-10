package openerp.openerpresourceserver.mapper;

import openerp.openerpresourceserver.dto.ScheduleVehicleAssignmentDto;
import openerp.openerpresourceserver.entity.ScheduleVehicleAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ScheduleVehicleAssignmentMapper {

    ScheduleVehicleAssignmentMapper INSTANCE = Mappers.getMapper(ScheduleVehicleAssignmentMapper.class);

    @Mapping(target = "assignedAt", source = "createdAt")
    ScheduleVehicleAssignmentDto assignmentToDto(ScheduleVehicleAssignment assignment);

    @Mapping(target = "createdAt", source = "assignedAt")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    ScheduleVehicleAssignment dtoToAssignment(ScheduleVehicleAssignmentDto dto);
}