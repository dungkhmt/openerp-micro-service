package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelProblemSubmissionDetailByTestCaseResponse {
    private UUID problemSubmissionDetailByTestCaseId;
    private String contestId;
    private String problemId;
    private String userLoginId;
    private UUID testCaseId;
    private String testCase;
    private String message;
    private int point;
    private String testCaseAnswer;
    private String participantAnswer;
    private Date createdAt;
    private String viewSubmitSolutionOutputMode;
}
