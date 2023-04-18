package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ModelGetTestCase {
    private boolean viewMore;
    private String testCase;
    private String correctAns;
    private int point;
    private UUID testCaseId;
    private String isPublic;
    private String status;
    private String description;
}
