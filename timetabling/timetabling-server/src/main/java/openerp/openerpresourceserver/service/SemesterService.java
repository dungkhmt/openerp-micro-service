package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.dto.request.SemesterDto;
import openerp.openerpresourceserver.model.entity.Semester;

import java.util.List;

public interface SemesterService {

    List<Semester> getSemester();

    void updateSemester(SemesterDto requestDto);

    Semester create(SemesterDto semesterDto);

    void deleteById(Long id);
}
