package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.Classroom;

import java.util.List;

public interface ClassroomService {

    List<Classroom> getClassroom();

    void updateClassroom();
}
