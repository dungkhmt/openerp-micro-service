package openerp.coderunnerservice.repo;

import openerp.coderunnerservice.entity.ContestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepo extends JpaRepository<ContestEntity, String> {
    ContestEntity findContestByContestId(String contestId);

    ContestEntity findContestEntityByContestIdAndUserId(String contestId, String userId);

}
