package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.dto.request.SemesterDto;
import openerp.openerpresourceserver.model.entity.Semester;

import java.util.List;

public interface SemesterService {

    List<Semester> getSemester();

    void updateSemester();

    Semester create(SemesterDto semesterDto);
}
