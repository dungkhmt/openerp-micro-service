package com.hust.baseweb.applications.programmingcontest.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModelGetTestCaseDetail {

    String testCase;

//     String problemId;

    String correctAns;

//     String problemDescription;
//
//     String problemSolution;

    int point;

    UUID testCaseId;

    String isPublic;

    String status;

    String description;
}
