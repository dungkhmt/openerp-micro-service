package openerp.openerpresourceserver.service;

import org.springframework.cache.annotation.Cacheable;
import openerp.openerpresourceserver.model.StudentSubmissionDetail;

import java.util.List;

public interface StudentSubmissionStatisticsService {

    List<StudentSubmissionDetail> getAllStatisticsDetailStudent();

    @Cacheable(cacheNames = "studentStatisticsCache")
    StudentSubmissionDetail getStatisticsDetailStudentId(String id);
}
