package openerp.openerpresourceserver.programmingcontest.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestRanking;
import openerp.openerpresourceserver.programmingcontest.model.ContestModelRepsonse;
import openerp.openerpresourceserver.programmingcontest.repo.ProgrammingContestRankingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProgrammingContestRankingServiceImpl implements  ProgrammingContestRankingService{
    private ProgrammingContestRankingRepo programmingContestRankingRepo;

    @Override
    public List<ProgrammingContestRanking> findAll() {
        //List<ProgrammingContestRanking> L = programmingContestRankingRepo.findAll();
        List<ProgrammingContestRanking> L = programmingContestRankingRepo.findAllSorted();


        return L;
    }

    @Override
    public List<ProgrammingContestRanking> findAllByContestId(String contestId) {
        List<ProgrammingContestRanking> L = programmingContestRankingRepo.findAllByContestIdSorted(contestId);
        return L;
    }

    @Override
    public List<ContestModelRepsonse> getContestAllIds() {
        List<String> contestIds = programmingContestRankingRepo.getAllContestIds();
        List<ContestModelRepsonse> res = new ArrayList<>();
        for(String cid: contestIds){
            res.add(new ContestModelRepsonse(cid,""));
        }
;        return res;
    }
}
