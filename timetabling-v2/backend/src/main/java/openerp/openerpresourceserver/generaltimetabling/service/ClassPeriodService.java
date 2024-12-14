package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.ClassPeriodDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassPeriod;

import java.util.List;

public interface ClassPeriodService {
    ClassPeriod create(ClassPeriodDto classPeriodDto);

    List<ClassPeriod> getClassPeriod();
}
