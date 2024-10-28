package openerp.openerpresourceserver.programmingcontest.repo;

import openerp.openerpresourceserver.programmingcontest.entity.CompositeProgrammingContestProblemRankingId;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestProblemRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgrammingContestProblemRankingRepo extends JpaRepository<ProgrammingContestProblemRanking, CompositeProgrammingContestProblemRankingId> {
    ProgrammingContestProblemRanking findByUserIdAndContestIdAndProblemId(String userId, String contestId, String problemId);
    List<ProgrammingContestProblemRanking> findByUserIdAndContestId(String userId, String contestId);
}
