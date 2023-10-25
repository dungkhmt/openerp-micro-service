package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.ClassCode;
import openerp.openerpresourceserver.model.entity.Institute;
import openerp.openerpresourceserver.model.entity.Semester;

import java.util.List;

public interface ScheduleService {
    List<Semester> getSemester();

    List<Institute> getInstitute();

    List<ClassCode> getClassCode();
}
