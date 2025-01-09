package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionDetailByTestcaseOM {

//     UUID contestSubmissionTestcaseId;
//
//    String contestId;
//
//    String problemId;
//
//    String userLoginId;

    UUID testCaseId;

    String testCase;

    String message;

    int point;

    String graded;// graded = Y means that the point is accounted to the grade

    Long runtime;

    Float memoryUsage;

    String testCaseAnswer;

    String participantAnswer;

    String stderr;

    Date createdAt;

    String viewSubmitSolutionOutputMode;
}
