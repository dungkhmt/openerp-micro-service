package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ModelProblemSubmissionDetailResponse {
    UUID problemSubmissionId;
    String problemId;
    String problemName;
    String submittedAt;
    String submissionSource;
    String submissionLanguage;
    int score;
    String testCasePass;
    String runTime;
    float memoryUsage;
    String status;
}
