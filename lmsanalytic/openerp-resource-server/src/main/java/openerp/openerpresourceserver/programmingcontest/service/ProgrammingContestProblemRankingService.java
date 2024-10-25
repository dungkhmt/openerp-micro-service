package openerp.openerpresourceserver.programmingcontest.service;

import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestProblemRanking;

import java.util.List;

public interface ProgrammingContestProblemRankingService {
    List<ProgrammingContestProblemRanking> findAll();
}
