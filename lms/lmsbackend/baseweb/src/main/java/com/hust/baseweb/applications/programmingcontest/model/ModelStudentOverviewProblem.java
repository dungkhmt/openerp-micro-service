package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ModelStudentOverviewProblem {

    private String problemId;
    private String problemName;
    private String problemCode;
    private String levelId;
    private boolean submitted = false;
    private boolean accepted = false;
    private Integer maxSubmittedPoint;
    private List<String> tags = new ArrayList<>();
}
