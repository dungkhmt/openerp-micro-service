package openerp.openerpresourceserver.tarecruitment.service;

import openerp.openerpresourceserver.tarecruitment.entity.dto.ChartDTO;
import openerp.openerpresourceserver.tarecruitment.entity.dto.PaginationDTO;
import openerp.openerpresourceserver.tarecruitment.entity.ClassCall;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ClassCallService {

    ClassCall createNewClass(ClassCall classCall);

    PaginationDTO<ClassCall> getAllClass(int page, int limit);

    Optional<ClassCall> getClassById(int id);

    PaginationDTO<ClassCall> getClassBySemester(String semester, String search, int page, int limit);

    ClassCall updateClass(int id, ClassCall classCall);

    boolean deleteClass(int id);

    boolean deleteMultipleClass(List<Integer> idList);

    List<ClassCall> getAllMyRegisteredClass(String userId, String semester);

    int importClass(MultipartFile file, String semester);

    List<ChartDTO> getNumberOfClassEachSemester();
}
