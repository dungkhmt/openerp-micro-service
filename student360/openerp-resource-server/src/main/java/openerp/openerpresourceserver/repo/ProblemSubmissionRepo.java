package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.ProblemSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemSubmissionRepo extends JpaRepository<ProblemSubmission,String> {


}
