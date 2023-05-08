package openerp.coderunnerservice.repo;


import openerp.coderunnerservice.entity.ContestSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContestSubmissionRepo extends JpaRepository<ContestSubmissionEntity, UUID> {

    ContestSubmissionEntity findContestSubmissionEntityByContestSubmissionId(UUID contestSubmissionId);
}
