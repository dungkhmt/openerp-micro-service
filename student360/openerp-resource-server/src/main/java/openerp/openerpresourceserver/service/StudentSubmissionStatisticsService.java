package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.StudentPerformance;
import openerp.openerpresourceserver.model.StudentStatisticContest;
import org.springframework.cache.annotation.Cacheable;
import openerp.openerpresourceserver.entity.StudentSubmissionStatistics;

import java.util.List;

public interface StudentSubmissionStatisticsService {

    List<StudentSubmissionStatistics> getAllStatisticsDetailStudent();
    StudentStatisticContest getStaticsContestStudentId(String id);
    @Cacheable(cacheNames = "studentStatisticsCache")
    StudentPerformance getPerformanceStudentId(String id);
}
