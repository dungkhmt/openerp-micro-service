package com.hust.baseweb.service.helper;

import com.hust.baseweb.applications.programmingcontest.entity.ContestEntity;
import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionTestCaseEntity;
import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import com.hust.baseweb.callexternalapi.model.ModelCreateContestSubmission;
import com.hust.baseweb.callexternalapi.service.ApiService;
import com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestConfig;
import com.hust.baseweb.constants.Constants;
import com.hust.baseweb.repo.ContestSubmissionRepo;
import com.hust.baseweb.repo.ContestSubmissionTestCaseEntityRepo;
import com.hust.baseweb.util.stringhandler.ProblemSubmission;
import com.hust.baseweb.util.stringhandler.StringHandler;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.*;

import static com.hust.baseweb.config.rabbitmq.ProblemContestRoutingKey.JUDGE_CUSTOM_PROBLEM;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class SubmissionResponseHandler {

    private ContestSubmissionRepo contestSubmissionRepo;
    private ContestSubmissionTestCaseEntityRepo contestSubmissionTestCaseEntityRepo;
    private RabbitTemplate rabbitTemplate;

    private ApiService apiService;

    @Transactional
    public void processSubmissionResponseV2(
            ContestEntity contest,
            List<TestCaseEntity> testCaseEntityList,
            List<String> listSubmissionResponse,
            ContestSubmissionEntity submission,
            String problemEvaluationType,
            int problemTimeLimit
    ) throws Exception {
        int runtime = 0;
        long score = 0;
        int nbTestCasePass = 0;
        int nbTestCaseGraded = 0;
        String totalStatus;
        String message = "";
        boolean compileError = false;
        boolean processing = false;

        int mb = 1000 * 1000;
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        int i = 0;

        long startTime1 = System.nanoTime();
        for (TestCaseEntity testCaseEntity : testCaseEntityList) {
            String response = listSubmissionResponse.get(i++);

            ProblemSubmission problemSubmission;

            try {

                if (Objects.equals(submission.getSourceCodeLanguage(), ContestSubmissionEntity.LANGUAGE_JAVA)) {
                    problemSubmission = StringHandler.handleContestResponseSingleTestcase(
                            response,
                            testCaseEntity.getCorrectAnswer(),
                            testCaseEntity.getTestCasePoint(),
                            problemEvaluationType,
                            problemTimeLimit);
                } else {
                    problemSubmission = StringHandler.handleContestResponseSingleTestcaseV2(
                            response,
                            testCaseEntity.getCorrectAnswer(),
                            testCaseEntity.getTestCasePoint(),
                            problemEvaluationType,
                            problemTimeLimit);
                }

                if (problemSubmission.getStatus()
                        .equals(ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR)) {
                    message = problemSubmission.getMessage();
                    compileError = true;
                    break;
                } else if (problemSubmission
                        .getStatus()
                        .equals(ContestSubmissionEntity.SUBMISSION_STATUS_WAIT_FOR_CUSTOM_EVALUATION)) {
                    processing = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("error from StringHandler");
            }
            boolean usedPointToGrade = true;
            if(contest.getEvaluateBothPublicPrivateTestcase()
                    .equals(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES)){
                // keep usedPointToGrade = true
            }else{
                if(testCaseEntity.getIsPublic().equals(TestCaseEntity.PUBLIC_YES)){
                    usedPointToGrade = false;// public testcase is not accounted/graded
                }else{
                    // keep usedPointToGrade = true for private testcase
                }
            }
            String usedToGrade = ContestSubmissionTestCaseEntity.USED_TO_GRADE_NO;
            runtime = runtime + problemSubmission.getRuntime().intValue();
            if(usedPointToGrade) {
                score = score + problemSubmission.getScore();
                nbTestCasePass += problemSubmission.getNbTestCasePass();
                nbTestCaseGraded += 1;
                usedToGrade = ContestSubmissionTestCaseEntity.USED_TO_GRADE_YES;
            }
            List<String> output = problemSubmission.getParticipantAns();
            String participantAns = output != null && !output.isEmpty() ? output.get(0) : "";

            ContestSubmissionTestCaseEntity cste = ContestSubmissionTestCaseEntity.builder()
                    .contestId(submission.getContestId())
                    .contestSubmissionId(submission.getContestSubmissionId())
                    .problemId(submission.getProblemId())
                    .testCaseId(testCaseEntity.getTestCaseId())
                    .submittedByUserLoginId(submission.getUserId())
                    .point(problemSubmission.getScore())
                    .status(StringHandler.removeNullCharacter(problemSubmission.getStatus()))
                    .participantSolutionOtput(StringHandler.removeNullCharacter(participantAns))
                    .runtime(problemSubmission.getRuntime())
                    .createdStamp(submission.getCreatedAt())
                    .usedToGrade(usedToGrade)
                    .build();

            long startTime = System.nanoTime();
            contestSubmissionTestCaseEntityRepo.saveAndFlush(cste);
            long endTime = System.nanoTime();
            log.debug(
                    "Save contestSubmissionTestCaseEntity to DB, execution time = {} ms",
                    (endTime - startTime) / 1000000);

        }

        long endTime1 = System.nanoTime();
        log.debug(
                "Total handle response time = {} ms",
                (endTime1 - startTime1) / 1000000);

        long used = memoryBean.getHeapMemoryUsage().getUsed() / mb;
        long committed = memoryBean.getHeapMemoryUsage().getCommitted() / mb;
        log.debug("Memory used / committed :  " + used + "mb / " + committed + "mb");

        if (compileError) {
            totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR;
        } else if (processing) {
            message = "Evaluating";
            totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_WAIT_FOR_CUSTOM_EVALUATION;
        } else if (nbTestCasePass == 0) {
            totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_FAILED;
        //} else if (nbTestCasePass > 0 && nbTestCasePass < testCaseEntityList.size()) {
        } else if (nbTestCasePass > 0 && nbTestCasePass < nbTestCaseGraded) {
            totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_PARTIAL;
        } else {
            message = "Successful";
            totalStatus = ContestSubmissionEntity.SUBMISSION_STATUS_ACCEPTED;
        }

        submission.setStatus(totalStatus);
        submission.setPoint(score);
        //submission.setTestCasePass(nbTestCasePass + " / " + testCaseEntityList.size());
        submission.setTestCasePass(nbTestCasePass + " / " + nbTestCaseGraded);
        submission.setSourceCode(submission.getSourceCode());
        submission.setSourceCodeLanguage(submission.getSourceCodeLanguage());
        submission.setRuntime((long) runtime);
        submission.setMessage(message);
        submission.setUpdateAt(new Date());
        contestSubmissionRepo.saveAndFlush(submission);

        ModelCreateContestSubmission m = new ModelCreateContestSubmission();
        m.setContestSubmissionId(submission.getContestSubmissionId());
        m.setContestId(submission.getContestId());
        m.setProblemId(submission.getProblemId());
        m.setParticipantUserId(submission.getUserId());
        m.setPoint(submission.getPoint());
        m.setTestCasePasses(submission.getTestCasePass());
        m.setSubmissionStatus(submission.getStatus());
        m.setMessage(submission.getMessage());
        m.setSourceCode(submission.getSourceCode());
        m.setSourceCodeLanguage(submission.getSourceCodeLanguage());
        m.setSubmissionCreatedStamp(submission.getCreatedAt());

        logAContestSubmission(m);

        if (processing) {
            rabbitTemplate.convertAndSend(
                    RabbitProgrammingContestConfig.EXCHANGE,
                    JUDGE_CUSTOM_PROBLEM,
                    submission.getContestSubmissionId());
        }
    }

    @Async
    private void logAContestSubmission(ModelCreateContestSubmission m){
        if (true) return;
        try {
            log.info("logAContestSubmission, submissionId = " + m.getContestSubmissionId());
            apiService.callLogContestSubmissionAPI("https://analytics.soict.ai/api/create-contest-submission", m);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Transactional
    public void processCustomSubmissionResponse(
            ContestSubmissionEntity submission,
            Map<UUID, String> evaluationResults
    ) {
        long totalPoint = 0;

        for (Map.Entry<UUID, String> result : evaluationResults.entrySet()) {
            UUID submissionTestCaseId = result.getKey();
            String response = result.getValue();
            ContestSubmissionTestCaseEntity submissionTestcase = contestSubmissionTestCaseEntityRepo
                    .findById(submissionTestCaseId)
                    .get();

            if (response == null) {
                submissionTestcase.setPoint(0);
                submissionTestcase.setStatus("Empty participant's program output");
            } else {
                int point = 0;
                String message = "";

                if (response.indexOf(' ') < 0) {
                    message =
                            "---Invalid response: Checker output not in format <SCORE> <MESSAGE>\n"
                                    + response;
                } else {
                    String pointString = response.substring(0, response.indexOf(' '));
//                    point = Integer.parseInt(pointString);
                    try {
                        point = Integer.parseInt(pointString);
                        message = response.substring(response.indexOf(' '),
                                response.indexOf(Constants.SPLIT_TEST_CASE));
                    } catch (NumberFormatException e) {
                        message =
                                "---Invalid response: Checker output not in format <SCORE> <MESSAGE>\n"
                                        + response;
                    }

                    if(submissionTestcase.getUsedToGrade()
                            .equals(ContestSubmissionTestCaseEntity.USED_TO_GRADE_YES)) {
                        totalPoint += point;
                    }else{
                        // do not account test-case to the final grade of the submission
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

        ModelCreateContestSubmission m = new ModelCreateContestSubmission();
        m.setContestSubmissionId(submission.getContestSubmissionId());
        m.setContestId(submission.getContestId());
        m.setProblemId(submission.getProblemId());
        m.setParticipantUserId(submission.getUserId());
        m.setPoint(submission.getPoint());
        m.setTestCasePasses(submission.getTestCasePass());
        m.setSubmissionStatus(submission.getStatus());
        m.setMessage(submission.getMessage());
        m.setSourceCode(submission.getSourceCode());
        m.setSourceCodeLanguage(submission.getSourceCodeLanguage());
        m.setSubmissionCreatedStamp(submission.getCreatedAt());

        logAContestSubmission(m);
    }

}
