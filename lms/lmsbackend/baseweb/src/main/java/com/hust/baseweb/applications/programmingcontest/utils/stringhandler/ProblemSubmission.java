package com.hust.baseweb.applications.programmingcontest.utils.stringhandler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemSubmission {

    private String status;
    private String message;
    private Long runtime;
    private String testCasePass;
    private int nbTestCasePass;
    private int score;
    private List<String> testCaseAns;
    private List<String> participantAns;
    private String scoreEvaluationType;
}
