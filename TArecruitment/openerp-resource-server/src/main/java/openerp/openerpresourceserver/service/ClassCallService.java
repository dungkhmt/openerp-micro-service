package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.ClassCall;

import java.util.List;
import java.util.Optional;

public interface ClassCallService {

    ClassCall createNewClass(ClassCall classCall);

    List<ClassCall> getAllClass();

    Optional<ClassCall> getClassById(int id);

    List<ClassCall> getClassBySemester(String semester);

    ClassCall updateClass(int id, ClassCall classCall);

    boolean deleteClass(int id);
}
