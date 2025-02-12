package com.hust.baseweb.applications.programmingcontest.model;

import com.hust.baseweb.applications.programmingcontest.entity.TagEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ModelCreateContestProblemResponse {

    private String problemId;

    private String problemName;

    private String problemDescription;

    private String userId;

    private int timeLimit;

    private int timeLimitCPP;

    private int timeLimitJAVA;

    private int timeLimitPYTHON;

    private int memoryLimit;

    private String levelId;

    private String categoryId;

    private String correctSolutionSourceCode;

    private String correctSolutionLanguage;

    private String solutionCheckerSourceCode;

    private String solutionCheckerSourceLanguage;

    private String scoreEvaluationType;

    private String solution;

    private Boolean isPreloadCode;

    private String preloadCode;

    private int levelOrder;

    private Date createdAt;

    private boolean isPublicProblem;

    private List<byte[]> attachment;

    private List<String> attachmentNames;

    private List<TagEntity> tags;

    private String status;

    private List<String> roles;

    private String sampleTestCase;
}

