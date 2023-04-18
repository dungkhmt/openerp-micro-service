package com.hust.baseweb.applications.programmingcontest.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ModelUpdateContestProblem {
    private String problemId;
    private String problemName;
    private String problemDescription;
    private int timeLimit;
    private int memoryLimit;
    private String levelId;
    private String categoryId;
    private String correctSolutionSourceCode;
    private String correctSolutionLanguage;
    private String solutionChecker;
    private String solutionCheckerLanguage;
    private String solution;
    private Boolean isPublic;
    private String scoreEvaluationType;
    private String[] fileId;
    private Integer[] tagIds;
    private List<String> removedFilesId;
}
