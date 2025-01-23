package com.hust.baseweb.service;

import com.hust.baseweb.applications.programmingcontest.entity.*;
import com.hust.baseweb.constants.ComputerLanguage;
import com.hust.baseweb.constants.Constants;
import com.hust.baseweb.model.ModelRunCodeFromIDE;
import com.hust.baseweb.repo.ContestSubmissionRepo;
import com.hust.baseweb.repo.ContestSubmissionTestCaseEntityRepo;
import com.hust.baseweb.service.helper.Judge0SubmissionResponseHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hust.soict.judge0client.config.Judge0Config;
import vn.edu.hust.soict.judge0client.entity.Judge0Submission;
import vn.edu.hust.soict.judge0client.service.Judge0Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Judge0ProblemTestCaseServiceImpl implements ProblemTestCaseService {

    ContestService contestService;

    ProblemService problemService;

    TestCaseService testCaseService;

    ContestSubmissionRepo contestSubmissionRepo;

    ContestSubmissionTestCaseEntityRepo contestSubmissionTestCaseEntityRepo;

    Judge0SubmissionResponseHandler submissionResponseHandler;

    Judge0Service judge0Service;

    Judge0Config judge0Config;

    @Override
    public String executableIDECode(
            ModelRunCodeFromIDE modelRunCodeFromIDE,
            String computerLanguage
    ) throws Exception {
        throw new NotImplementedException();
    }

    /**
     * @param submissionId
     * @throws Exception
     */
    @Override
    public void submitContestProblemTestCaseByTestCaseWithFileProcessor(UUID submissionId) throws Exception {
        ContestSubmissionEntity submission = contestSubmissionRepo.findByContestSubmissionId(submissionId);
        ProblemEntity problem = problemService.findProblemWithCache(submission.getProblemId());
        ContestEntity contest = contestService.findContestWithCache(submission.getContestId());

        boolean evaluatePrivatePublic = contest != null &&
                contest.getEvaluateBothPublicPrivateTestcase() != null &&
                contest.getEvaluateBothPublicPrivateTestcase()
                        .equals(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES);

        List<TestCaseEntity> testCases = testCaseService.findListTestCaseWithCache(submission.getProblemId(), evaluatePrivatePublic)
                .stream()
                .filter(tc -> tc.getStatusId() == null || !tc.getStatusId().equals(TestCaseEntity.STATUS_DISABLED))
                .collect(Collectors.toList());

        List<Judge0Submission> testcasesEvaluationResult = new ArrayList<>();
        int timeLimitByLanguage = getTimeLimitByLanguage(problem, submission.getSourceCodeLanguage());
        for (TestCaseEntity tc : testCases) {
            Judge0Submission result = submission(
                    submission.getSourceCode(),
                    submission.getSourceCodeLanguage(),
                    tc,
                    timeLimitByLanguage,
                    problem.getMemoryLimit(),
                    problem.getScoreEvaluationType());
            testcasesEvaluationResult.add(result);
            if (result.getStatus().getId() == 6) { // if the first testcase be Compile Error then it's not necessary to judge the remain testcases
                break;
            }
        }

        submissionResponseHandler.processSubmissionResponseV2(
                contest,
                testCases,
                testcasesEvaluationResult,
                submission,
                problem.getScoreEvaluationType());
    }

    /**
     * @param submissionId
     * @throws Exception
     */
    @Override
    @Transactional
    public void evaluateCustomProblemSubmission(UUID submissionId) throws Exception {
        ContestSubmissionEntity submission = contestSubmissionRepo.findByContestSubmissionId(submissionId);
        ProblemEntity problem = problemService.findProblemWithCache(submission.getProblemId());
        ContestEntity contest = contestService.findContestWithCache(submission.getContestId());
        List<ContestSubmissionTestCaseEntity> submissionTestcases = contestSubmissionTestCaseEntityRepo.findAllByContestSubmissionIdOrderByCreatedStampDesc(submissionId);

        boolean includePublicAndPrivateTest = contest != null &&
                contest.getEvaluateBothPublicPrivateTestcase() != null &&
                contest.getEvaluateBothPublicPrivateTestcase()
                        .equals(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES);

        List<TestCaseEntity> testcases = testCaseService.findListTestCaseWithCache(submission.getProblemId(), includePublicAndPrivateTest)
                .stream()
                .filter(tc1 -> tc1.getStatusId() == null || !tc1.getStatusId().equals(TestCaseEntity.STATUS_DISABLED))
                .collect(Collectors.toList());

        Map<ContestSubmissionTestCaseEntity, Judge0Submission> evaluationResults = new HashMap<>();
        for (TestCaseEntity tc : testcases) {
            ContestSubmissionTestCaseEntity submissionTestcase = submissionTestcases
                    .stream()
                    .filter(st -> st.getTestCaseId().equals(tc.getTestCaseId()))
                    .findFirst()
                    .get();

            if (contest.getEvaluateBothPublicPrivateTestcase().equals(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES)) {
                submissionTestcase.setUsedToGrade(ContestSubmissionTestCaseEntity.USED_TO_GRADE_YES);
            } else {
                if (tc.getIsPublic().equals(TestCaseEntity.PUBLIC_NO)) {// always account private testcase
                    submissionTestcase.setUsedToGrade(ContestSubmissionTestCaseEntity.USED_TO_GRADE_YES);
                } else {
                    submissionTestcase.setUsedToGrade(ContestSubmissionTestCaseEntity.USED_TO_GRADE_NO);
                }
            }

            // Only evaluate the output of test cases without errors
            Judge0Submission result;
            if (ContestSubmissionEntity.SUBMISSION_STATUS_WAIT_FOR_CUSTOM_EVALUATION.equals(submissionTestcase.getStatus())) {
                if (StringUtils.isBlank(submissionTestcase.getParticipantSolutionOutput())) {
                    result = null;
                } else {
                    result = judgeSubmissionTestCaseOutput(
                            problem.getSolutionCheckerSourceCode(),
                            problem.getSolutionCheckerSourceLanguage(),
                            submissionTestcase.getParticipantSolutionOutput(),
                            tc,
                            getTimeLimitByLanguage(problem, problem.getSolutionCheckerSourceLanguage()),
                            problem.getMemoryLimit());

                    // TODO: IMPORTANT: enhance this check, maybe provide the checker error to the user
                    if (result.getStatus().getId() != 3) {
                        throw new RuntimeException("Error occurred while checker evaluated the submission test case output with id: " + submissionTestcase.getContestSubmissionTestcaseId());
                        // TODO: gửi thông báo lỗi về giao diện và ghi nhận lỗi vào DB, không ném exception
                    }
                }
            } else {
                result = null;
            }

            evaluationResults.put(submissionTestcase, result);
        }

        submissionResponseHandler.processCustomSubmissionResponse(submission, evaluationResults);
    }

    /**
     * @param sourceCode
     * @param computerLanguage
     * @param testCase
     * @param timeLimit
     * @param memoryLimit
     * @return
     * @throws Exception
     */
    private Judge0Submission submission(
            String sourceCode,
            String computerLanguage,
            TestCaseEntity testCase,
            int timeLimit,
            int memoryLimit,
            String evaluationType
    ) throws Exception {
        int languageId;
        String compilerOptions = null;
        switch (ComputerLanguage.Languages.valueOf(computerLanguage)) {
            case C:
                languageId = 50;
                compilerOptions = "-std=c17 -w -O2 -lm -fmax-errors=3";
                break;
            case CPP11:
                languageId = 54;
                compilerOptions = "-std=c++11 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
                break;
            case CPP14:
                languageId = 54;
                compilerOptions = "-std=c++14 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
                break;
            case CPP:
            case CPP17:
                languageId = 54;
                compilerOptions = "-std=c++17 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
                break;
            case JAVA:
                languageId = 62; // Xem xét JAVA_OPTS giới hạn bộ nhớ nhưng có vẻ không cần thiết lắm với Judge0
                break;
            case PYTHON3:
                languageId = 71;
                break;
            default:
                throw new Exception("Language not supported");
        }

        Judge0Submission submission = Judge0Submission.builder()
                .sourceCode(sourceCode)
                .languageId(languageId)
                .compilerOptions(compilerOptions)
                .commandLineArguments(null)
                .stdin(testCase.getTestCase())
                .expectedOutput(Constants.ProblemResultEvaluationType.CUSTOM.getValue().equals(evaluationType) ? null : testCase.getCorrectAnswer())
                .cpuTimeLimit((float) timeLimit)
                .cpuExtraTime((float) (timeLimit * 1.0 + 2.0))
                .wallTimeLimit((float) (timeLimit * 1.0 + 10.0))
                .memoryLimit((float) memoryLimit * 1024)
                .stackLimit(judge0Config.getSubmission().getMaxStackLimit())
                .maxProcessesAndOrThreads(languageId != 62 ? 2 : 20) // TODO: adjust this config for multi-thread program
                .enablePerProcessAndThreadTimeLimit(false)
                .enablePerProcessAndThreadMemoryLimit(false)
                .maxFileSize(judge0Config.getSubmission().getMaxMaxFileSize())
                .redirectStderrToStdout(false)
                .enableNetwork(false)
                .numberOfRuns(1)
                .build();

//        submission.encodeBase64();
        submission = judge0Service.createASubmission(submission, true, true);
        submission.decodeBase64();

        return submission;
    }

    /**
     * @param sourceChecker
     * @param computerLanguage
     * @param submissionTestCaseOutput
     * @param testCase
     * @param timeLimit
     * @param memoryLimit
     * @return
     * @throws Exception
     */
    private Judge0Submission judgeSubmissionTestCaseOutput(
            String sourceChecker,
            String computerLanguage,
            String submissionTestCaseOutput,
            TestCaseEntity testCase,
            int timeLimit,
            int memoryLimit
    ) throws Exception {
        int languageId;
        String compilerOptions = null;
        switch (ComputerLanguage.Languages.valueOf(computerLanguage)) {
            case C:
                languageId = 50;
                compilerOptions = "-std=c17 -w -O2 -lm -fmax-errors=3";
                break;
            case CPP11:
                languageId = 54;
                compilerOptions = "-std=c++11 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
                break;
            case CPP14:
                languageId = 54;
                compilerOptions = "-std=c++14 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
                break;
            case CPP:
            case CPP17:
                languageId = 54;
                compilerOptions = "-std=c++17 -w -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456";
                break;
            case JAVA:
                languageId = 62;
                break;
            case PYTHON3:
                languageId = 71;
                break;
            default:
                throw new Exception("Language not supported");
        }

        Judge0Submission submission = Judge0Submission.builder()
                .sourceCode(sourceChecker)
                .languageId(languageId)
                .compilerOptions(compilerOptions)
                .commandLineArguments(null)
                .stdin(String.join("\n", new String[]{
                        testCase.getTestCase(),
                        testCase.getCorrectAnswer(),
                        submissionTestCaseOutput}))
                .cpuTimeLimit((float) timeLimit)
                .cpuExtraTime((float) (timeLimit * 1.0 + 2.0))
                .wallTimeLimit((float) (timeLimit * 1.0 + 10.0))
                .memoryLimit((float) memoryLimit * 1024)
                .stackLimit(judge0Config.getSubmission().getMaxStackLimit())
                .maxProcessesAndOrThreads(languageId != 62 ? 2 : 20) // OK, chấm output thì không cần đa luồng, trừ Java
                .enablePerProcessAndThreadTimeLimit(false)
                .enablePerProcessAndThreadMemoryLimit(false)
                .maxFileSize(judge0Config.getSubmission().getMaxMaxFileSize())
                .redirectStderrToStdout(false)
                .enableNetwork(false)
                .numberOfRuns(1)
                .build();

        submission = judge0Service.createASubmission(submission, true, true);
        submission.decodeBase64();

        return submission;
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
