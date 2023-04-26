package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelProgrammingContestUploadTestCase {
    private String problemId;
    private String isPublic;
    private int point;
    private String correctAnswer;
    private String description;
    private String uploadMode;
}
