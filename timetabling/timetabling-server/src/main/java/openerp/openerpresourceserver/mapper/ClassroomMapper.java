package openerp.openerpresourceserver.mapper;

import openerp.openerpresourceserver.model.dto.request.ClassroomDto;
import openerp.openerpresourceserver.model.dto.request.SemesterDto;
import openerp.openerpresourceserver.model.entity.Classroom;
import openerp.openerpresourceserver.model.entity.Semester;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClassroomMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Classroom mapDtoToEntity(ClassroomDto classroomDto) {
        return modelMapper.map(classroomDto, Classroom.class);
    }

    public ClassroomDto mapEntityToDto(Classroom classroom) {
        return modelMapper.map(classroom, ClassroomDto.class);
    }
}
