package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.StudentPerformance;
import openerp.openerpresourceserver.model.StudentStatisticContest;
import org.springframework.cache.annotation.Cacheable;
import openerp.openerpresourceserver.model.StudentSubmissionDetail;

import java.util.List;

public interface StudentSubmissionStatisticsService {

    List<StudentSubmissionDetail> getAllStatisticsDetailStudent();

    @Cacheable(cacheNames = "studentStatisticsCache")
    StudentSubmissionDetail getStatisticsDetailStudentId(String id);

    StudentStatisticContest getStaticsContestStudentId(String id);
    StudentPerformance getPerformanceStudentId(String id);
}
