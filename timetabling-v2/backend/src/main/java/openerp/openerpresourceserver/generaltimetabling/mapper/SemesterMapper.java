package openerp.openerpresourceserver.generaltimetabling.mapper;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.SemesterDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Semester;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SemesterMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Semester mapDtoToEntity(SemesterDto semesterDto) {
        return modelMapper.map(semesterDto, Semester.class);
    }

    public SemesterDto mapEntityToDto(Semester semester) {
        return modelMapper.map(semester, SemesterDto.class);
    }
}
