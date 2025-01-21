package com.hust.baseweb.util.stringhandler;

import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import com.hust.baseweb.constants.Constants;
import vn.edu.hust.soict.judge0client.entity.Judge0Submission;

import java.util.Collections;

public class Judge0StringHandler {

    /**
     * @param testcaseEvaluationResult
     * @param point
     * @param evaluationType
     * @return
     */
    public static ProblemSubmission handleContestResponseSingleTestcaseV2(
            Judge0Submission testcaseEvaluationResult,
            int point,
            String evaluationType
    ) {
        String status = null;
        int cnt = 0;
        int score = 0;

        switch (testcaseEvaluationResult.getStatus().getId()) {
            case 3:
                if (Constants.ProblemResultEvaluationType.NORMAL.getValue().equals(evaluationType)
                        || evaluationType == null
                        || evaluationType.isEmpty()
                ) { // Constants.ProblemResultEvaluationType.NORMAL
                    status = testcaseEvaluationResult.getStatus().getDescription();
                    score += point;
                    cnt++;
                } else if (Constants.ProblemResultEvaluationType.CUSTOM.getValue().equals(evaluationType)) {
                    status = ContestSubmissionEntity.SUBMISSION_STATUS_WAIT_FOR_CUSTOM_EVALUATION;
                }

                break;
            case 4:
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
            case 12:
            case 13:
            case 14:
                status = testcaseEvaluationResult.getStatus().getDescription();
                break;
            case 6:
                return buildCompileErrorForSubmission(testcaseEvaluationResult);
            case 11:
                String output = (testcaseEvaluationResult.getRedirectStderrToStdout() == null
                        || testcaseEvaluationResult.getRedirectStderrToStdout())
                        ? testcaseEvaluationResult.getStdout()
                        : testcaseEvaluationResult.getStderr();

                if (testcaseEvaluationResult.getMemory() > testcaseEvaluationResult.getMemoryLimit()) { // Chỉ phát hiện trường hợp tổng bộ nhớ quá giới hạn
                    status = ContestSubmissionEntity.SUBMISSION_STATUS_MEMORY_LIMIT_EXCEEDED;
                } else if (output.contains(Constants.TestCaseSubmissionError.MEMORY_ERROR.getValue())) {
                    status = ContestSubmissionEntity.SEGMENTATION_FAULT;
                } else if (output.contains(Constants.TestCaseSubmissionError.FILE_LIMIT.getValue())) { // OK
                    status = ContestSubmissionEntity.SUBMISSION_STATUS_OUTPUT_LIMIT_EXCEEDED;
                } else {
                    status = testcaseEvaluationResult.getStatus().getDescription();
                }

                break;
        }

        return ProblemSubmission.builder()
                .runtime((long) (testcaseEvaluationResult.getTime() * 1_000))
                .memory(testcaseEvaluationResult.getMemory())
                .score(score)
                .status(status)
//                .compileOutput(testcaseEvaluationResult.getCompileOutput())
//                .testCasePass(cnt + " / " + 1) // consider removing
                .nbTestCasePass(cnt)
                .participantAns(Collections.singletonList(testcaseEvaluationResult.getStdout()))
                .stderr(testcaseEvaluationResult.getStderr())
                .build();
    }

    /**
     * @param testcaseEvaluationResult
     * @return
     */
    private static ProblemSubmission buildCompileErrorForSubmission(Judge0Submission testcaseEvaluationResult) {
        return ProblemSubmission.builder()
                .score(0)
                .runtime(0L)
                .memory(testcaseEvaluationResult.getMemory())
//                .testCasePass("0")
                .status(ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR)
                .compileOutput(testcaseEvaluationResult.getCompileOutput())
                .build();
    }

    /**
     * TODO: đánh giá lại xem có cần thiết không
     *
     * @param s
     * @return
     */
    public static String removeNullCharacter(String s) {
        return s == null ? null : s.replaceAll("\u0000", "");
    }
}


