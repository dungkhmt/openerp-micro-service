package openerp.openerpresourceserver.programmingcontest.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestProblemRanking;
import openerp.openerpresourceserver.programmingcontest.repo.ProgrammingContestProblemRankingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProgrammingContestProblemRankingServiceImpl implements ProgrammingContestProblemRankingService {
    private ProgrammingContestProblemRankingRepo programmingContestProblemRankingRepo;

    @Override
    public List<ProgrammingContestProblemRanking> findAll() {
        List<ProgrammingContestProblemRanking> res = programmingContestProblemRankingRepo.findAll();

        return res;
    }
}

