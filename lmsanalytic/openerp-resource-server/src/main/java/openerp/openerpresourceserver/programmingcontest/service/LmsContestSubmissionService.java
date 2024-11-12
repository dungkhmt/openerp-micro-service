package openerp.openerpresourceserver.programmingcontest.service;

import openerp.openerpresourceserver.programmingcontest.entity.LmsContestSubmission;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestRanking;
import openerp.openerpresourceserver.programmingcontest.model.ModelCreateContestSubmission;
import java.util.List;

public interface LmsContestSubmissionService {
    LmsContestSubmission save(ModelCreateContestSubmission m);
    List<LmsContestSubmission> findAll();
    List<LmsContestSubmission> findSubmissionsByUserSubmissionId(String userSubmissionId);
}
