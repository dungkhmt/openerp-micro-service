package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.StudentSubmissionStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentSubmissionStatisticsRepo extends JpaRepository<StudentSubmissionStatistics, String> {

    List<StudentSubmissionStatistics> findAllByOrderByTotalSubmittedDesc();
}
