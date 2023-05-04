package openerp.coderunnerservice.repo;

import openerp.coderunnerservice.entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepo extends JpaRepository<ProblemEntity, String> {

    ProblemEntity findByProblemId(String problemId);

}
