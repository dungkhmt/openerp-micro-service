package openerp.openerpresourceserver.mapper;

import openerp.openerpresourceserver.model.dto.request.ClassPeriodDto;
import openerp.openerpresourceserver.model.entity.ClassPeriod;
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
