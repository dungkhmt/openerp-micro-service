package openerp.openerpresourceserver.programmingcontest.service;

import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestRanking;
import openerp.openerpresourceserver.programmingcontest.model.ContestModelRepsonse;

import java.util.List;

public interface ProgrammingContestRankingService {
    List<ProgrammingContestRanking> findAll();
    List<ProgrammingContestRanking> findAllByContestId(String contestId);


    List<ContestModelRepsonse> getContestAllIds();
}
