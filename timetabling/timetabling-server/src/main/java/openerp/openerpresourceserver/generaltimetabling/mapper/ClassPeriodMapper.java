package openerp.openerpresourceserver.generaltimetabling.mapper;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.ClassPeriodDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassPeriod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClassPeriodMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ClassPeriod mapDtoToEntity(ClassPeriodDto classPeriodDto) {
        return modelMapper.map(classPeriodDto, ClassPeriod.class);
    }

    public ClassPeriodDto mapEntityToDto(ClassPeriod classPeriod) {
        return modelMapper.map(classPeriod, ClassPeriodDto.class);
    }
}
