package openerp.coderunnerservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.coderunnerservice.constants.Constants;
import openerp.coderunnerservice.docker.DockerClientBase;
import openerp.coderunnerservice.entity.*;
import openerp.coderunnerservice.model.ModelRunCodeFromIDE;
import openerp.coderunnerservice.repo.ContestSubmissionRepo;
import openerp.coderunnerservice.repo.ContestSubmissionTestCaseEntityRepo;
import openerp.coderunnerservice.service.helper.SubmissionResponseHandler;
import openerp.coderunnerservice.util.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProblemTestCaseServiceImpl implements ProblemTestCaseService {

    private ContestService contestService;
    private ProblemService problemService;
    private TestCaseService testCaseService;
    private ContestSubmissionRepo contestSubmissionRepo;
    private ContestSubmissionTestCaseEntityRepo contestSubmissionTestCaseEntityRepo;
    private TempDir tempDir;
    private SubmissionResponseHandler submissionResponseHandler;
    private DockerClientBase dockerClientBase;

    @Override
    public String executableIDECode(
            ModelRunCodeFromIDE modelRunCodeFromIDE,
            String computerLanguage
    ) throws Exception {
        String tempName = tempDir.createRandomScriptFileName(Math.random() + "-" + computerLanguage);
        String response = runCode(
                modelRunCodeFromIDE.getSource(),
                computerLanguage,
                tempName,
                modelRunCodeFromIDE.getInput(),
                modelRunCodeFromIDE.getTimeLimit(),
                "Language Not Found");
        tempDir.pushToConcurrentLinkedQueue(tempName);
        return response;
    }

    private String runCode(
            String sourceCode,
            String computerLanguage,
            String tempName,
            String input,
            int timeLimit,
            String exception
    ) throws Exception {
        String ans;
        tempName = tempName.replaceAll(" ", "");
        switch (computerLanguage) {
            case "CPP":
                tempDir.createScriptFile(sourceCode, input, timeLimit, Constants.Languages.CPP, tempName);
                ans = dockerClientBase.runExecutable(Constants.Languages.CPP, tempName);
                break;
            case "JAVA":
                tempDir.createScriptFile(sourceCode, input, timeLimit, Constants.Languages.JAVA, tempName);
                ans = dockerClientBase.runExecutable(Constants.Languages.JAVA, tempName);
                break;
            case "PYTHON3":
                tempDir.createScriptFile(sourceCode, input, timeLimit, Constants.Languages.PYTHON3, tempName);
                ans = dockerClientBase.runExecutable(Constants.Languages.PYTHON3, tempName);
                break;
            default:
                throw new Exception(exception);
        }
//        tempDir.pushToConcurrentLinkedQueue(tempName);
        return ans;
    }

    @Override
    public void submitContestProblemTestCaseByTestCaseWithFileProcessor(
            UUID submissionId
    ) throws Exception {

        ContestSubmissionEntity submission = contestSubmissionRepo.findContestSubmissionEntityByContestSubmissionId(submissionId);
        ProblemEntity problem = problemService.findProblemWithCache(submission.getProblemId());
        ContestEntity contest = contestService.findContestWithCache(submission.getContestId());

        String userId = submission.getUserId();

        List<TestCaseEntity> testCaseEntityList = null;
        boolean evaluatePrivatePublic = contest != null &&
                contest.getEvaluateBothPublicPrivateTestcase() != null &&
                contest.getEvaluateBothPublicPrivateTestcase()
                        .equals(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES);

        testCaseEntityList = testCaseService.findListTestCaseWithCache(
                submission.getProblemId(),
                evaluatePrivatePublic);

        List<TestCaseEntity> listTestCaseAvailable = new ArrayList<>();
        for (TestCaseEntity tc : testCaseEntityList) {
            if (tc.getStatusId() != null && tc.getStatusId().equals(TestCaseEntity.STATUS_DISABLED)) {
                continue;
            }
            listTestCaseAvailable.add(tc);
        }
        testCaseEntityList = listTestCaseAvailable;

        String tempName = tempDir.createRandomScriptFileName(userId + "-" +
                submission.getContestId() + "-" +
                submission.getProblemId() + "-" +
                Math.random());

        List<String> listSubmissionResponse = new ArrayList<>();
        for (TestCaseEntity testCaseEntity : testCaseEntityList) {
            List<TestCaseEntity> L = new ArrayList();
            L.add(testCaseEntity);

            String response = submission(
                    submission.getSourceCode(),
                    submission.getSourceCodeLanguage(),
                    tempName,
                    L,
                    "language not found",
                    problem.getTimeLimit(),
                    problem.getMemoryLimit());

            listSubmissionResponse.add(response);
        }

        tempDir.removeDir(tempName);
        submissionResponseHandler.processSubmissionResponse(
                testCaseEntityList,
                listSubmissionResponse,
                submission,
                problem.getScoreEvaluationType());
    }

    @Override
    public void evaluateCustomProblemSubmission(UUID submissionId) throws Exception {

        ContestSubmissionEntity submission = contestSubmissionRepo.findContestSubmissionEntityByContestSubmissionId(submissionId);
        ProblemEntity problemEntity = problemService.findProblemWithCache(submission.getProblemId());
        ContestEntity contest = contestService.findContestWithCache(submission.getContestId());
        List<ContestSubmissionTestCaseEntity> submissionTestCases = contestSubmissionTestCaseEntityRepo.findAllByContestSubmissionId(submissionId);

        String userId = submission.getUserId();

        List<TestCaseEntity> testCaseEntityList;
        boolean evaluatePrivatePublic = contest != null &&
                contest.getEvaluateBothPublicPrivateTestcase() != null &&
                contest.getEvaluateBothPublicPrivateTestcase()
                        .equals(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES);

        testCaseEntityList = testCaseService.findListTestCaseWithCache(
                submission.getProblemId(),
                evaluatePrivatePublic);

        List<TestCaseEntity> listTestCaseAvailable = new ArrayList<>();
        for (TestCaseEntity tc : testCaseEntityList) {
            if (tc.getStatusId() != null && tc.getStatusId().equals(TestCaseEntity.STATUS_DISABLED)) {
                continue;
            }
            listTestCaseAvailable.add(tc);
        }
        testCaseEntityList = listTestCaseAvailable;

        String tempName = tempDir.createRandomScriptFileName(userId + "-" +
                submission.getContestId() + "-" +
                submission.getProblemId() + "-" +
                "custom-" +
                Math.random());

        Map<UUID, String> submissionResponses = new HashMap<>();
        for (TestCaseEntity testCaseEntity : testCaseEntityList) {
            ContestSubmissionTestCaseEntity submissionTestCase = submissionTestCases
                    .stream()
                    .filter(tc -> tc.getTestCaseId().equals(testCaseEntity.getTestCaseId()))
                    .findFirst().get();

            String response = submissionSolutionOutput(
                    problemEntity.getSolutionCheckerSourceCode(),
                    problemEntity.getSolutionCheckerSourceLanguage(),
                    submissionTestCase.getParticipantSolutionOtput(),
                    tempName,
                    testCaseEntity,
                    "language not found",
                    problemEntity.getTimeLimit(),
                    problemEntity.getMemoryLimit());

            submissionResponses.put(submissionTestCase.getContestSubmissionTestcaseId(), response);
        }

        tempDir.removeDir(tempName);
        submissionResponseHandler.processCustomSubmissionResponse(submission, submissionResponses);
    }

    private String submission(
            String source,
            String computerLanguage,
            String tempName,
            List<TestCaseEntity> testCaseList,
            String exception,
            int timeLimit,
            int memoryLimit
    ) throws Exception {
        String ans;
        tempName = tempName.replace(" ", "");
        switch (computerLanguage) {
            case "CPP":
                tempDir.createScriptSubmissionFile(
                        Constants.Languages.CPP,
                        tempName,
                        testCaseList,
                        source,
                        timeLimit,
                        memoryLimit);
                ans = dockerClientBase.runExecutable(Constants.Languages.CPP, tempName);
                break;
            case "JAVA":
                tempDir.createScriptSubmissionFile(
                        Constants.Languages.JAVA,
                        tempName,
                        testCaseList,
                        source,
                        timeLimit,
                        memoryLimit);
                ans = dockerClientBase.runExecutable(Constants.Languages.JAVA, tempName);
                break;
            case "PYTHON3":
                tempDir.createScriptSubmissionFile(
                        Constants.Languages.PYTHON3,
                        tempName,
                        testCaseList,
                        source,
                        timeLimit,
                        memoryLimit);
                ans = dockerClientBase.runExecutable(Constants.Languages.PYTHON3, tempName);
                break;
            case "GOLANG":
                tempDir.createScriptSubmissionFile(
                        Constants.Languages.GOLANG,
                        tempName,
                        testCaseList,
                        source,
                        timeLimit,
                        memoryLimit);
                ans = dockerClientBase.runExecutable(Constants.Languages.GOLANG, tempName);
                break;
            default:
                throw new Exception(exception);
        }
//        tempDir.pushToConcurrentLinkedQueue(tempName);
        return ans;
    }

    private String submissionSolutionOutput(
            String sourceChecker,
            String computerLanguage,
            String solutionOutput,
            String tempName,
            TestCaseEntity testCase,
            String exception,
            int timeLimit,
            int memoryLimit
    ) throws Exception {
        //log.info("submissionSolutionOutput, sourceChecker = " + sourceChecker + " solutionOutput = " + solutionOutput + " testCase = " + testCase.getTestCase());

        String ans = "";
        tempName = tempName.replaceAll(" ", "");
        switch (computerLanguage) {
            case "CPP":
                tempDir.createScriptSubmissionSolutionOutputFile(
                        Constants.Languages.CPP,
                        tempName,
                        solutionOutput,
                        testCase,
                        sourceChecker,
                        timeLimit);
                ans = dockerClientBase.runExecutable(Constants.Languages.CPP, tempName);
                //  log.info("submissionSolutionOutput, sourceChecker = " + sourceChecker + " solutionOutput = " + solutionOutput + " testCase = " + testCase.getTestCase()
                //  + " ans = " + ans);
                break;
            case "JAVA":
                break;
            case "PYTHON3":
                break;
            case "GOLANG":
                break;
            default:
                throw new Exception(exception);
        }
//        tempDir.pushToConcurrentLinkedQueue(tempName);
        return ans;
    }

}
