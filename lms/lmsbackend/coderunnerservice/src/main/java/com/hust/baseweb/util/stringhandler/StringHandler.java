package com.hust.baseweb.util.stringhandler;

import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import lombok.extern.slf4j.Slf4j;
import com.hust.baseweb.constants.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

@Slf4j
public class StringHandler {

    public static ProblemSubmission handleContestResponseSingleTestcase(
            String response,
            String testCaseAns,
            int point,
            String problemEvaluationType,
            int problemTimeLimit
    ) {
        String status = "";

        if (response.endsWith(ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR + "\n")) {
            return buildCompileErrorForSubmission(response);
        }

        response = response.trim();
        int lastNewlineIndex = response.lastIndexOf('\n');
        int secondLastNewlineIndex = response.lastIndexOf('\n', lastNewlineIndex - 1);

        int runtime = Integer.parseInt(response.substring(secondLastNewlineIndex + 1, lastNewlineIndex).trim());

        // get testcase answer of participant
        String participantAns = response.substring(0, response.indexOf(Constants.SPLIT_TEST_CASE));

        int cnt = 0;
        int score = 0;

        String participantTestcaseAns = replaceSpace(participantAns);

        if (problemEvaluationType == null || problemEvaluationType.isEmpty())
            problemEvaluationType = Constants.ProblemResultEvaluationType.NORMAL.getValue();

        if (participantTestcaseAns.equals(Constants.TestCaseSubmissionError.TIME_LIMIT.getValue())) {
            status = ContestSubmissionEntity.SUBMISSION_STATUS_TIME_LIMIT_EXCEEDED;
            participantAns = status;
            runtime = problemTimeLimit * 1000;
        } else if (participantTestcaseAns.equals(Constants.TestCaseSubmissionError.FILE_LIMIT.getValue())) {
            status = ContestSubmissionEntity.SUBMISSION_STATUS_OUTPUT_LIMIT_EXCEEDED;
            participantAns = status;
        } else if (participantTestcaseAns.equals(Constants.TestCaseSubmissionError.MEMORY_LIMIT.getValue())) {
            status = ContestSubmissionEntity.SEGMENTATION_FAULT;
            participantAns = status;
        } else {
            String correctTestcaseAns = replaceSpace(testCaseAns);
            if (problemEvaluationType == null || problemEvaluationType.isEmpty())
                problemEvaluationType = Constants.ProblemResultEvaluationType.NORMAL.getValue();

            if (problemEvaluationType.equals(Constants.ProblemResultEvaluationType.NORMAL.getValue())) {
                if (!correctTestcaseAns.equals(participantTestcaseAns)) {
                    status = ContestSubmissionEntity.SUBMISSION_STATUS_WRONG;
                } else {
                    status = ContestSubmissionEntity.SUBMISSION_STATUS_ACCEPTED;
                    score += point;
                    cnt++;
                }
            } else if (problemEvaluationType.equals(Constants.ProblemResultEvaluationType.CUSTOM.getValue())) {
                status = ContestSubmissionEntity.SUBMISSION_STATUS_WAIT_FOR_CUSTOM_EVALUATION;
            }
        }

        return ProblemSubmission.builder()
                .runtime((long) runtime)
                .score(score)
                .status(status)
                .message(response)
                .testCasePass(cnt + " / " + 1)
                .nbTestCasePass(cnt)
                .participantAns(Collections.singletonList(participantAns))
                .build();
    }

    public static ProblemSubmission handleContestResponseSingleTestcaseV2(
            String response,
            String testCaseAns,
            int point,
            String problemEvaluationType,
            int problemTimeLimit
    ) {

        response = response.trim();

        int lastNewlineIndex = response.lastIndexOf('\n');

        String status = response.substring(lastNewlineIndex + 1).trim();

        if (status.equals(ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR)) {
            return buildCompileErrorForSubmission(response);
        } else if (status.equals(Constants.TestCaseSubmissionError.TIME_LIMIT.getValue())) {
            status = ContestSubmissionEntity.SUBMISSION_STATUS_TIME_LIMIT_EXCEEDED;
        } else if (status.equals(Constants.TestCaseSubmissionError.MEMORY_LIMIT.getValue())) {
            status = ContestSubmissionEntity.SEGMENTATION_FAULT;
        } else if (status.equals(Constants.TestCaseSubmissionError.FILE_LIMIT.getValue())) {
            status = ContestSubmissionEntity.SUBMISSION_STATUS_OUTPUT_LIMIT_EXCEEDED;
        }

        int runtime;
        if (status.equals(ContestSubmissionEntity.SUBMISSION_STATUS_TIME_LIMIT_EXCEEDED)) {
            runtime = problemTimeLimit * 1000;
        } else {
            int secondLastNewlineIndex = response.lastIndexOf('\n', lastNewlineIndex - 1);
            runtime = Integer.parseInt(response.substring(secondLastNewlineIndex + 1, lastNewlineIndex).trim());
        }

        // get testcase answer of participant
        String participantAns = response.substring(0, response.indexOf(Constants.SPLIT_TEST_CASE));

        int cnt = 0;
        int score = 0;


        if (problemEvaluationType == null || problemEvaluationType.isEmpty())
            problemEvaluationType = Constants.ProblemResultEvaluationType.NORMAL.getValue();

        if (problemEvaluationType.equals(Constants.ProblemResultEvaluationType.NORMAL.getValue())) {
            if (status.equals("Successful")) {
                String participantTestcaseAns = replaceSpace(participantAns);
                String correctTestcaseAns = replaceSpace(testCaseAns);

                if (!correctTestcaseAns.equals(participantTestcaseAns)) {
                    status = ContestSubmissionEntity.SUBMISSION_STATUS_WRONG;
                } else {
                    status = ContestSubmissionEntity.SUBMISSION_STATUS_ACCEPTED;
                    score += point;
                    cnt++;
                }
            }
        } else if (problemEvaluationType.equals(Constants.ProblemResultEvaluationType.CUSTOM.getValue())) {
            status = ContestSubmissionEntity.SUBMISSION_STATUS_WAIT_FOR_CUSTOM_EVALUATION;
        }

        return ProblemSubmission.builder()
                .runtime((long) runtime)
                .score(score)
                .status(status)
                .message(response)
                .testCasePass(cnt + " / " + 1)
                .nbTestCasePass(cnt)
                .participantAns(Collections.singletonList(participantAns))
                .build();
    }

    private static ProblemSubmission buildCompileErrorForSubmission(String message) {
        return ProblemSubmission.builder()
                .score(0)
                .runtime(0L)
                .testCasePass("0")
                .status(ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR)
                .message(message)
                .build();
    }

    private static String replaceSpace(String s) {
        if (s == null) {
            return null;
        }

        s = s.replaceAll("\n", " ");
        return s.replaceAll("( +)", " ").trim();

    }

    private static String replaceSpaceV2(String s) {
        if (s == null) return null;

        return StringUtils.replace(s, "\n", " ").trim();

//        TextStringBuilder stringBuilder = new TextStringBuilder(s);
//
//        stringBuilder.replaceAll("\n", " ");
//        stringBuilder.replaceAll("( +)", " ").trim();
//        return stringBuilder.toString();

    }

    public static String removeNullCharacter(String s) {
        return s.replaceAll("\u0000", "");
    }
}

