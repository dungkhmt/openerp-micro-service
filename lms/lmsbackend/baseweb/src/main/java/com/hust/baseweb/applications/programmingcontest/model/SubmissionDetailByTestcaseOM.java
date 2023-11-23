package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionDetailByTestcaseOM {

//    private UUID contestSubmissionTestcaseId;
//
//    private String contestId;
//
//    private String problemId;
//
//    private String userLoginId;

    private UUID testCaseId;

    private String testCase;

    private String message;

    private int point;

    private Long runtime;

    private String testCaseAnswer;

    private String participantAnswer;

    private Date createdAt;

    private String viewSubmitSolutionOutputMode;
}
