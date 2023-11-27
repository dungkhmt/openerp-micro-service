package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.mapper.ClassPeriodMapper;
import openerp.openerpresourceserver.model.dto.request.ClassPeriodDto;
import openerp.openerpresourceserver.model.dto.request.ClassroomDto;
import openerp.openerpresourceserver.model.entity.ClassPeriod;
import openerp.openerpresourceserver.model.entity.Classroom;
import openerp.openerpresourceserver.repo.ClassPeriodRepo;
import openerp.openerpresourceserver.service.ClassCodeService;
import openerp.openerpresourceserver.service.ClassPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassPeriodServiceImpl implements ClassPeriodService {

    @Autowired
    private ClassPeriodMapper classPeriodMapper;

    @Autowired
    private ClassPeriodRepo classPeriodRepo;

    @Override
    public List<ClassPeriod> getClassPeriod() {
        return classPeriodRepo.findAll();
    }

    @Override
    public ClassPeriod create(ClassPeriodDto classPeriodDto) {
        ClassPeriod classPeriod = classPeriodMapper.mapDtoToEntity(classPeriodDto);
        classPeriodRepo.save(classPeriod);
        return classPeriod;
    }
}
