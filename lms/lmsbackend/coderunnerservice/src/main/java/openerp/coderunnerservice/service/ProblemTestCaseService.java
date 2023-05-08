package openerp.coderunnerservice.service;

import openerp.coderunnerservice.model.ModelRunCodeFromIDE;

import java.util.UUID;

public interface ProblemTestCaseService {

    String executableIDECode(
            ModelRunCodeFromIDE modelRunCodeFromIDE,
            String computerLanguage
    ) throws Exception;

    void submitContestProblemTestCaseByTestCaseWithFileProcessor(UUID contestSubmissionId) throws Exception;

    void evaluateCustomProblemSubmission(UUID submissionId) throws Exception;
}
