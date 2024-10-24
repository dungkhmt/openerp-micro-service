package openerp.openerpresourceserver.programmingcontest.repo;

import openerp.openerpresourceserver.programmingcontest.entity.LmsContestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LmsContestSubmissionRepo extends JpaRepository<LmsContestSubmission, UUID> {
}
