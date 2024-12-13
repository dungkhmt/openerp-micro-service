package openerp.openerpresourceserver.generaltimetabling.service.impl;

import openerp.openerpresourceserver.generaltimetabling.mapper.ClassPeriodMapper;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.ClassPeriodDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassPeriod;
import openerp.openerpresourceserver.generaltimetabling.repo.ClassPeriodRepo;
import openerp.openerpresourceserver.generaltimetabling.service.ClassPeriodService;
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
