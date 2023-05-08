package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemSubmissionResponse {
    private UUID problemSubmissionId;
    private String timeSubmitted;
    private String status;
    private int score;
    private String runtime;
    private float memoryUsage;
    private String language;
}
