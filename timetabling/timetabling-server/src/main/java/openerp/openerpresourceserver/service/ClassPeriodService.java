package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.dto.request.ClassPeriodDto;
import openerp.openerpresourceserver.model.entity.ClassPeriod;
import openerp.openerpresourceserver.model.entity.Classroom;

import java.util.List;

public interface ClassPeriodService {
    ClassPeriod create(ClassPeriodDto classPeriodDto);

    List<ClassPeriod> getClassPeriod();
}
