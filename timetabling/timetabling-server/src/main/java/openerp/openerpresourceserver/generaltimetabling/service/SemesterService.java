package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.SemesterDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Semester;

import java.util.List;

public interface SemesterService {

    List<Semester> getSemester();

    void updateSemester(SemesterDto requestDto);

    Semester create(SemesterDto semesterDto);

    void deleteById(Long id);
}
