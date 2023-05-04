package openerp.coderunnerservice.service;

import java.util.UUID;

public interface ProblemTestCaseService {

    void submitContestProblemTestCaseByTestCaseWithFileProcessor(UUID contestSubmissionId) throws Exception;

    void evaluateCustomProblemSubmission(UUID submissionId) throws Exception;
}
