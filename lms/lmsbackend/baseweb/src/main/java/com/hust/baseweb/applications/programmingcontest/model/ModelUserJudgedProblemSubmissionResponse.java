package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelUserJudgedProblemSubmissionResponse {
    private String userId;
    private String fullName;
    private String problemId;
    private String problemName;
    private String submissionSourceCode;
    private int point;
    private String testCasePassed;
    private String status;
}
