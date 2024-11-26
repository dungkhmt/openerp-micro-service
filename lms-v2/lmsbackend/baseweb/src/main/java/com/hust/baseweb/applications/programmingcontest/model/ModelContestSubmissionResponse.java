package com.hust.baseweb.applications.programmingcontest.model;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelContestSubmissionResponse {

    UUID contestSubmissionID;
    String contestId;
    String problemId;
    UUID selectedTestCaseId; // optional for re-run, check submit an output of a test-case
    String problemName;
    Date submittedAt;
    Long score;
    String testCasePass;
    int numberTestCasePassed;
    int totalNumberTestCase;
    Float memoryUsage;
    String status;
    String message;
    long runtime;
}
