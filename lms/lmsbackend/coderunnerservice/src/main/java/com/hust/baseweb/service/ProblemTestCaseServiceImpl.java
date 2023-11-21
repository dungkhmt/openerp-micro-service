package com.hust.baseweb.service;

import com.hust.baseweb.applications.programmingcontest.entity.*;
import com.hust.baseweb.constants.ComputerLanguage;
import com.hust.baseweb.docker.DockerClientBase;
import com.hust.baseweb.model.ModelRunCodeFromIDE;
import com.hust.baseweb.repo.ContestSubmissionRepo;
import com.hust.baseweb.repo.ContestSubmissionTestCaseEntityRepo;
import com.hust.baseweb.service.helper.SubmissionResponseHandler;
import com.hust.baseweb.util.TempDir;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        switch (ComputerLanguage.Languages.valueOf(computerLanguage)) {
            case C:
            case CPP:
            case CPP11:
            case CPP14:
            case CPP17:
                tempDir.createScriptFile(sourceCode, input, timeLimit, ComputerLanguage.Languages.CPP17, tempName);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.CPP17, tempName);
                break;
            case JAVA:
                tempDir.createScriptFile(sourceCode, input, timeLimit, ComputerLanguage.Languages.JAVA, tempName);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.JAVA, tempName);
                break;
            case PYTHON3:
                tempDir.createScriptFile(sourceCode, input, timeLimit, ComputerLanguage.Languages.PYTHON3, tempName);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.PYTHON3, tempName);
                break;
            default:
                throw new Exception(exception);
        }
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
//                    problem.getTimeLimit(),
                    getTimeLimitByLanguage(problem, submission.getSourceCodeLanguage()),
                    problem.getMemoryLimit());

            listSubmissionResponse.add(response);
        }

        tempDir.removeDir(tempName);

        if (Objects.equals(submission.getSourceCodeLanguage(), ContestSubmissionEntity.LANGUAGE_PYTHON)) {
            submissionResponseHandler.processSubmissionResponseNewPythonVersion(
                    testCaseEntityList,
                    listSubmissionResponse,
                    submission,
                    problem.getScoreEvaluationType(),
                    getTimeLimitByLanguage(problem, submission.getSourceCodeLanguage()));
            return;
        }

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
//                    problemEntity.getTimeLimit(),
                    getTimeLimitByLanguage(problemEntity, problemEntity.getSolutionCheckerSourceLanguage()),
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
        switch (ComputerLanguage.Languages.valueOf(computerLanguage)) {
            case C:
                tempDir.createScriptSubmissionFile(
                        ComputerLanguage.Languages.C,
                        tempName,
                        testCaseList,
                        source,
                        timeLimit,
                        memoryLimit);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.C, tempName);
                break;
            case CPP11:
                tempDir.createScriptSubmissionFile(
                        ComputerLanguage.Languages.CPP11,
                        tempName,
                        testCaseList,
                        source,
                        timeLimit,
                        memoryLimit);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.CPP11, tempName);
                break;
            case CPP14:
                tempDir.createScriptSubmissionFile(
                        ComputerLanguage.Languages.CPP14,
                        tempName,
                        testCaseList,
                        source,
                        timeLimit,
                        memoryLimit);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.CPP14, tempName);
                break;
            case CPP:
            case CPP17:
                tempDir.createScriptSubmissionFile(
                        ComputerLanguage.Languages.CPP17,
                        tempName,
                        testCaseList,
                        source,
                        timeLimit,
                        memoryLimit);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.CPP17, tempName);
                break;
            case JAVA:
                tempDir.createScriptSubmissionFile(
                        ComputerLanguage.Languages.JAVA,
                        tempName,
                        testCaseList,
                        source,
                        timeLimit,
                        memoryLimit);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.JAVA, tempName);
                break;
            case PYTHON3:
                tempDir.createScriptSubmissionFile(
                        ComputerLanguage.Languages.PYTHON3,
                        tempName,
                        testCaseList,
                        source,
                        timeLimit,
                        memoryLimit);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.PYTHON3, tempName);
                break;
            default:
                throw new Exception(exception);
        }
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

        String ans = "";
        tempName = tempName.replaceAll(" ", "");
        switch (ComputerLanguage.Languages.valueOf(computerLanguage)) {
            case C:
            case CPP:
            case CPP11:
            case CPP14:
            case CPP17:
                tempDir.createScriptSubmissionSolutionOutputFile(
                        ComputerLanguage.Languages.CPP17,
                        tempName,
                        solutionOutput,
                        testCase,
                        sourceChecker,
                        timeLimit);
                ans = dockerClientBase.runExecutable(ComputerLanguage.Languages.CPP17, tempName);
                break;
            case JAVA:
            case PYTHON3:
                break;
            default:
                throw new Exception(exception);
        }
        return ans;
    }

    private int getTimeLimitByLanguage(ProblemEntity problem, String language) {
        int timeLimit;
        switch (language) {
            case ContestSubmissionEntity.LANGUAGE_CPP:
                timeLimit = problem.getTimeLimitCPP();
                break;
            case ContestSubmissionEntity.LANGUAGE_JAVA:
                timeLimit = problem.getTimeLimitJAVA();
                break;
            case ContestSubmissionEntity.LANGUAGE_PYTHON:
                timeLimit = problem.getTimeLimitPYTHON();
                break;
            default:
                timeLimit = problem.getTimeLimitCPP();
        }
        return timeLimit;
    }

}
