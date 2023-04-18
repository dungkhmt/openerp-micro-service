package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ModelProblemSubmissionResponse {
    private UUID problemSubmissionId;
    private String timeSubmitted;
    private String status;
    private int score;
    private String runtime;
    private float memoryUsage;
    private String language;
    private String result;
    private String problemName;
}
