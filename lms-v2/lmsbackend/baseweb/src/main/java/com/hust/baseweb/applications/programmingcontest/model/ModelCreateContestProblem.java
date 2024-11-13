package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ModelCreateContestProblem {

    private String problemId;
    private String problemName;
    private String problemDescription;
    private int timeLimit;
    private int timeLimitCPP;
    private int timeLimitJAVA;
    private int timeLimitPYTHON;
    private int memoryLimit;
    private String levelId;
    private String categoryId;
    private String correctSolutionSourceCode;
    private String correctSolutionLanguage;
    private String solutionChecker;
    private String solutionCheckerLanguage;
    private String solution;
    private Boolean isPreloadCode;
    private String preloadCode;
    private Boolean isPublic;
    private String scoreEvaluationType;
    private String[] fileId;
    private Integer[] tagIds;
    private String sampleTestCase;
}
