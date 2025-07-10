package openerp.openerpresourceserver.repo;


import openerp.openerpresourceserver.entity.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestProblemRepo extends JpaRepository<ContestProblem, String> {

    int countByContestId(String contestId);

}
