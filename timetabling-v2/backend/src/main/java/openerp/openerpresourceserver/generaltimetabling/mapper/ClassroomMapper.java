package openerp.openerpresourceserver.generaltimetabling.mapper;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.ClassroomDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
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
