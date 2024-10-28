package openerp.openerpresourceserver.programmingcontest.service;

import openerp.openerpresourceserver.programmingcontest.entity.LmsContestSubmission;
import openerp.openerpresourceserver.programmingcontest.model.ModelCreateContestSubmission;

public interface LmsContestSubmissionService {
    LmsContestSubmission save(ModelCreateContestSubmission m);
}
