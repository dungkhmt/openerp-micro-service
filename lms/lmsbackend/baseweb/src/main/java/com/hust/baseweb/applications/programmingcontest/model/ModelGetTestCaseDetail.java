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
public class ModelGetTestCaseDetail {
    private UUID testCaseId;
    private String problemId;
    private String correctAns;
    private String testCase;
    private String problemDescription;
    private String problemSolution;
    private int point;
    private String isPublic;
    private String description;
}
