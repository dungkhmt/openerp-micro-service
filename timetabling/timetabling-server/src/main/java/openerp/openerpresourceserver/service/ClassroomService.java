package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.dto.request.ClassroomDto;
import openerp.openerpresourceserver.model.dto.request.SemesterDto;
import openerp.openerpresourceserver.model.entity.Classroom;
import openerp.openerpresourceserver.model.entity.Semester;

import java.util.List;

public interface ClassroomService {

    List<Classroom> getClassroom();

    List<String> getBuilding();

    void updateClassroom(ClassroomDto requestDto);

    Classroom create(ClassroomDto classroomDto);

    void deleteById(Long id);

    void deleteByIds(List<Long> ids);
}
