package openerp.openerpresourceserver.programmingcontest.service;

import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestRanking;

import java.util.List;

public interface ProgrammingContestRankingService {
    List<ProgrammingContestRanking> findAll();
}
