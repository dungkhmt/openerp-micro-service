package openerp.openerpresourceserver.programmingcontest.repo;

import openerp.openerpresourceserver.programmingcontest.entity.CompositeProgrammingContestRankingId;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProgrammingContestRankingRepo extends JpaRepository<ProgrammingContestRanking, CompositeProgrammingContestRankingId> {
    @Query(value = "select * from programming_contest_ranking order by point desc",nativeQuery = true)
    List<ProgrammingContestRanking> findAllSorted();

    @Query(value = "select distinct contest_id from programming_contest_ranking",nativeQuery = true)
    List<String> getAllContestIds();
    @Query(value = "select * from programming_contest_ranking where contest_id = ?1 order by point desc",nativeQuery = true)
    List<ProgrammingContestRanking> findAllByContestIdSorted(String contestId);

    ProgrammingContestRanking findByUserIdAndContestId(String userId, String contestId);
}
