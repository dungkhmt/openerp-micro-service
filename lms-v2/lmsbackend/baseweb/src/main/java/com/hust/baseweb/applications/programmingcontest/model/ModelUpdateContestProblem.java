package com.hust.baseweb.applications.programmingcontest.model;

import com.hust.baseweb.applications.programmingcontest.entity.ProblemStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModelUpdateContestProblem {

    String problemId;

    String problemName;

    String problemDescription;

    float timeLimit;

    float timeLimitCPP;

    float timeLimitJAVA;

    float timeLimitPYTHON;

    float memoryLimit;

    String levelId;

    String categoryId;

    String correctSolutionSourceCode;

    String correctSolutionLanguage;

    String solutionChecker;

    String solutionCheckerLanguage;

    String solution;

    Boolean isPreloadCode;

    String preloadCode;

    Boolean isPublic;

    String scoreEvaluationType;

    String[] fileId;

    Integer[] tagIds;

    List<String> removedFilesId;

    ProblemStatus status;

    String sampleTestCase;
}
