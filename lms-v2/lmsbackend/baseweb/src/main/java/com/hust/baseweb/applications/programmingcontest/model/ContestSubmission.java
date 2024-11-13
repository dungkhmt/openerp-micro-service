package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContestSubmission {

    private UUID contestSubmissionId;
    private String problemId;
    private String problemName;
    private String contestId;
    private String userId;
    private String fullname;
    private String affiliation;
    private String testCasePass;
    private String sourceCodeLanguage;
    private Long point;
    private String status;
    private String message;
    private String createAt;
    private Date submissionDate;
    private String managementStatus;
    private String violationForbiddenInstruction;
    private String violationForbiddenInstructionMessage;

}
