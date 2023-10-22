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
            String problemEvaluationType
    ) {
        // log.info("handleContestResponse, response {}", response);

        String status = "";

//        // remove the last '\n' character, which is redundant
//        response = response.substring(0, response.length() - 1);

//        // get status ("testcasedone" / "Compile Error")
//        int statusIndex = response.lastIndexOf("\n") + 1;
//        String status = response.substring(statusIndex);
//
//        if (status.contains(ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR)) {
//            return buildCompileErrorForSubmission(1, originalMessage);
//        }

//        response = response.substring(0, statusIndex - 1);
//        int runTimeIndex = response.lastIndexOf("\n") + 1;
//        String runtimeString = response.substring(runTimeIndex);
//        Long runtime = Long.parseLong(runtimeString);

        if (response.endsWith(ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR + "\n")) {
            return buildCompileErrorForSubmission(1, response);
        }

        response = response.trim();
        int lastNewlineIndex = response.lastIndexOf('\n');
        int secondLastNewlineIndex = response.lastIndexOf('\n', lastNewlineIndex - 1);

        String runtime = response.substring(secondLastNewlineIndex + 1, lastNewlineIndex).trim();

        // get testcase answer of participant
        String participantAns = response.substring(
                0,
                response.indexOf(Constants.SPLIT_TEST_CASE));

        int cnt = 0;
        int score = 0;

        String participantTestcaseAns = replaceSpace(participantAns);
//        String participantTestcaseAns = participantAns;

        if (participantTestcaseAns.equals(Constants.TestCaseSubmissionError.TIME_LIMIT.getValue())) {
            status = ContestSubmissionEntity.SUBMISSION_STATUS_TIME_LIMIT_EXCEEDED;
            participantAns = status;
        } else if (participantTestcaseAns.equals(Constants.TestCaseSubmissionError.FILE_LIMIT.getValue())) {
            status = ContestSubmissionEntity.SUBMISSION_STATUS_OUTPUT_LIMIT_EXCEEDED;
            participantAns = status;
        } else if (participantTestcaseAns.equals(Constants.TestCaseSubmissionError.MEMORY_LIMIT.getValue())) {
            status = ContestSubmissionEntity.SEGMENTATION_FAULT;
            participantAns = status;
        } else {
            String correctTestcaseAns = replaceSpace(testCaseAns);
//            String correctTestcaseAns = testCaseAns;
            if (problemEvaluationType == null || problemEvaluationType.equals(""))
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
                .runtime(Long.parseLong(runtime))
                .score(score)
                .status(status)
                .message(response)
                .testCasePass(cnt + " / " + 1)
                .nbTestCasePass(cnt)
                .participantAns(Collections.singletonList(participantAns))
                .build();
    }

    private static ProblemSubmission buildCompileErrorForSubmission(int numTestCases, String message) {
        return ProblemSubmission.builder()
                .score(0)
                .runtime(0L)
                .testCasePass(0 + " / " + numTestCases)
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

