package com.hust.baseweb.applications.programmingcontest.model;

import com.hust.baseweb.applications.programmingcontest.entity.TagEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModelCreateContestProblemResponse {

    String problemId;

    String problemName;

    String problemDescription;

    String userId;

    float timeLimit;

    float timeLimitCPP;

    float timeLimitJAVA;

    float timeLimitPYTHON;

    float memoryLimit;

    String levelId;

    String categoryId;

    String correctSolutionSourceCode;

    String correctSolutionLanguage;

    String solutionCheckerSourceCode;

    String solutionCheckerSourceLanguage;

    String scoreEvaluationType;

    String solution;

    Boolean isPreloadCode;

    String preloadCode;

    int levelOrder;

    Date createdAt;

    boolean isPublicProblem;

    List<byte[]> attachment;

    List<String> attachmentNames;

    List<TagEntity> tags;

    String status;

    List<String> roles;

    String sampleTestCase;
}

