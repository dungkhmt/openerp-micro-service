package com.hust.baseweb.service.helper;

import com.hust.baseweb.applications.programmingcontest.entity.ContestEntity;
import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionTestCaseEntity;
import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestConfig;
import com.hust.baseweb.repo.ContestSubmissionRepo;
import com.hust.baseweb.repo.ContestSubmissionTestCaseEntityRepo;
import com.hust.baseweb.util.stringhandler.Judge0StringHandler;
import com.hust.baseweb.util.stringhandler.ProblemSubmission;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hust.soict.judge0client.entity.Judge0Submission;

import java.util.*;

import static com.hust.baseweb.config.rabbitmq.ProblemContestRoutingKey.JUDGE_CUSTOM_PROBLEM;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class Judge0SubmissionResponseHandler {

    private ContestSubmissionRepo contestSubmissionRepo;

    private ContestSubmissionTestCaseEntityRepo contestSubmissionTestCaseEntityRepo;

    private RabbitTemplate rabbitTemplate;

    /**
     * @param contest
     * @param testCases
     * @param testcasesEvaluationResult
     * @param submission
     * @param evaluationType
     */
    @Transactional
    public void processSubmissionResponseV2(
            ContestEntity contest,
            List<TestCaseEntity> testCases,
            List<Judge0Submission> testcasesEvaluationResult,
            ContestSubmissionEntity submission,
            String evaluationType
    ) {
        long totalWallTime = 0;
        long score = 0;
        int nbTestCasePass = 0;
        int nbTestCaseGraded = 0;
        String submissionStatus;
        String message = "";
        boolean compileError = false;
        boolean processing = false;
        int i = 0;
        List<ContestSubmissionTestCaseEntity> submissionTestCases = new ArrayList<>();

        for (TestCaseEntity tc : testCases) {
            Judge0Submission result = testcasesEvaluationResult.get(i++);
            ProblemSubmission problemSubmission = Judge0StringHandler.handleContestResponseSingleTestcaseV2(
                    result,
                    tc.getTestCasePoint(),
                    evaluationType
            );

            if (ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR.equals(problemSubmission.getStatus())) {
                message = problemSubmission.getCompileOutput();
                compileError = true;
                break;
            } else if (ContestSubmissionEntity.SUBMISSION_STATUS_WAIT_FOR_CUSTOM_EVALUATION.equals(problemSubmission.getStatus())) {
                processing = true;
            }

            totalWallTime += problemSubmission.getRuntime();
            boolean usedPointToGrade = ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES.equals(contest.getEvaluateBothPublicPrivateTestcase())
                    || !TestCaseEntity.PUBLIC_YES.equals(tc.getIsPublic()); // public testcase is not accounted/graded
            if (usedPointToGrade) {
                score += problemSubmission.getScore();
                nbTestCasePass += problemSubmission.getNbTestCasePass();
                nbTestCaseGraded++;
            }

            List<String> output = problemSubmission.getParticipantAns();
            String participantAns = output != null && !output.isEmpty() ? output.get(0) : "";

            ContestSubmissionTestCaseEntity submissionTestCase = ContestSubmissionTestCaseEntity.builder()
                    .contestId(submission.getContestId())
                    .contestSubmissionId(submission.getContestSubmissionId())
                    .problemId(submission.getProblemId())
                    .testCaseId(tc.getTestCaseId())
                    .submittedByUserLoginId(submission.getUserId())
                    .point(problemSubmission.getScore())
                    .status(Judge0StringHandler.removeNullCharacter(problemSubmission.getStatus()))
                    .participantSolutionOutput(Judge0StringHandler.removeNullCharacter(participantAns))
                    .stderr(problemSubmission.getStderr())
                    .runtime(problemSubmission.getRuntime())
                    .memoryUsage(problemSubmission.getMemory())
                    .judge0SubmissionToken(UUID.fromString(result.getToken()))
//                    .createdStamp(submission.getCreatedAt()) // TODO: xem lại chỗ này lấy từ submission là đúng chưa, có vẻ sai
                    .usedToGrade(usedPointToGrade ? ContestSubmissionTestCaseEntity.USED_TO_GRADE_YES : ContestSubmissionTestCaseEntity.USED_TO_GRADE_NO)
                    .build();

            submissionTestCases.add(submissionTestCase);
        }

        contestSubmissionTestCaseEntityRepo.saveAllAndFlush(submissionTestCases);

        if (compileError) {
            submissionStatus = ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR;
        } else if (processing) {
            message = "Evaluating";
            submissionStatus = ContestSubmissionEntity.SUBMISSION_STATUS_WAIT_FOR_CUSTOM_EVALUATION;
        } else if (nbTestCasePass == 0) {
            submissionStatus = ContestSubmissionEntity.SUBMISSION_STATUS_FAILED;
            //} else if (nbTestCasePass > 0 && nbTestCasePass < testCases.size()) {
        } else if (nbTestCasePass > 0 && nbTestCasePass < nbTestCaseGraded) {
            submissionStatus = ContestSubmissionEntity.SUBMISSION_STATUS_PARTIAL;
        } else {
            message = "Successful";
            submissionStatus = ContestSubmissionEntity.SUBMISSION_STATUS_ACCEPTED;
        }

        submission.setStatus(submissionStatus);
        submission.setPoint(score);
        //submission.setTestCasePass(nbTestCasePass + " / " + testCases.size());
        submission.setTestCasePass(nbTestCasePass + " / " + nbTestCaseGraded);
        submission.setRuntime(totalWallTime);
        submission.setMessage(message);
        submission.setUpdateAt(new Date());

        contestSubmissionRepo.saveAndFlush(submission);

        if (processing) {
            rabbitTemplate.convertAndSend(
                    RabbitProgrammingContestConfig.EXCHANGE,
                    JUDGE_CUSTOM_PROBLEM,
                    submission.getContestSubmissionId());
        }
    }

    /**
     * @param submission
     * @param evaluationResults
     */
    @Transactional
    public void processCustomSubmissionResponse(
            ContestSubmissionEntity submission,
            Map<ContestSubmissionTestCaseEntity, Judge0Submission> evaluationResults
    ) {
        long totalPoint = 0;

        for (Map.Entry<ContestSubmissionTestCaseEntity, Judge0Submission> result : evaluationResults.entrySet()) {
            ContestSubmissionTestCaseEntity submissionTestcase = result.getKey();
            Judge0Submission res = result.getValue();

            if (res == null) {
                submissionTestcase.setPoint(0);

                if (ContestSubmissionEntity.SUBMISSION_STATUS_WAIT_FOR_CUSTOM_EVALUATION.equals(submissionTestcase.getStatus())) {
                    submissionTestcase.setStatus("Empty participant's program output");
                }
            } else {
                int point = 0;
                String message;
                String response = res.getStdout();

                if (response.indexOf(' ') < 0) {
                    message =
                            "---Invalid response: Checker output not in format <SCORE> <MESSAGE>\n"
                                    + response;
                } else {
                    String pointString = response.substring(0, response.indexOf(' '));

                    try {
                        point = Integer.parseInt(pointString);
                        message = response.substring(response.indexOf(' '));
                    } catch (NumberFormatException e) {
                        message =
                                "---Invalid response: Checker output not in format <SCORE> <MESSAGE>\n"
                                        + response;
                    }

                    if (ContestSubmissionTestCaseEntity.USED_TO_GRADE_YES.equals(submissionTestcase.getUsedToGrade())) {
                        totalPoint += point;
                    }
                }

                submissionTestcase.setPoint(point);
                submissionTestcase.setStatus(message);
            }

            contestSubmissionTestCaseEntityRepo.save(submissionTestcase);
        }

        submission.setPoint(totalPoint);
        submission.setStatus(ContestSubmissionEntity.SUBMISSION_STATUS_CUSTOM_EVALUATED);
        submission.setMessage(ContestSubmissionEntity.SUBMISSION_STATUS_CUSTOM_EVALUATED);
        submission.setTestCasePass("_ / " + evaluationResults.size());
        submission.setUpdateAt(new Date());

        contestSubmissionRepo.save(submission);
    }
}

