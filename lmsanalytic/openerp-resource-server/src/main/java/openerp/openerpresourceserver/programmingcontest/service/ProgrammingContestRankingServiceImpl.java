package openerp.openerpresourceserver.programmingcontest.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestRanking;
import openerp.openerpresourceserver.programmingcontest.repo.ProgrammingContestRankingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProgrammingContestRankingServiceImpl implements  ProgrammingContestRankingService{
    private ProgrammingContestRankingRepo programmingContestRankingRepo;

    @Override
    public List<ProgrammingContestRanking> findAll() {
        List<ProgrammingContestRanking> L = programmingContestRankingRepo.findAll();

        return L;
    }
}
