package openerp.openerpresourceserver.labtimetabling.service;

import openerp.openerpresourceserver.labtimetabling.entity.Class;

import java.util.List;
import java.util.Optional;

public interface ClassService {
    List<Class> getAllClasses();
    List<Class> getClassesBySemester(Long semId);
    Class getClassById(Long id);
    Class createClass(Class _class);
    Optional<Class> patchClass(Long id, Class _class);
    boolean deleteClass(Long id);
    List<String> getAllSemesters();
    int batchInsert(List<Class> classes);
}
