package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.StudentPerformance;
import org.springframework.cache.annotation.Cacheable;

public interface StudentPerformanceService {
    @Cacheable(cacheNames = "studentStatisticsCache")
    StudentPerformance getPerformanceStudentId(String id);
}
