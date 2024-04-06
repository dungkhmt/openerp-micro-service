package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.PaginationDTO;
import openerp.openerpresourceserver.entity.ClassCall;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClassCallService {

    ClassCall createNewClass(ClassCall classCall);

    PaginationDTO<ClassCall> getAllClass(int page, int limit);

    Optional<ClassCall> getClassById(int id);

    PaginationDTO<ClassCall> getClassBySemester(String semester, int page, int limit);

    ClassCall updateClass(int id, ClassCall classCall);

    boolean deleteClass(int id);

    List<ClassCall> getAllMyRegisteredClass(String userId, String semester);
}
