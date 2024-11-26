package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class  ModelUpdateContest {
    private String contestName;
    private long contestSolvingTime;
    private List<String> problemIds;
    private Date startedAt;
    private long countDownTime;
    private String statusId;
    private String submissionActionType;
    private int maxNumberSubmission;
    private String participantViewResultMode;
    private String problemDescriptionViewType;
    private String useCacheContestProblem;
    private String evaluateBothPublicPrivateTestcase;
    private int maxSourceCodeLength;
    private int minTimeBetweenTwoSubmissions;
    private String judgeMode;
    private String sendConfirmEmailUponSubmission;
    private String participantViewSubmissionMode;
    private List<String> languagesAllowed;
    private String contestType;
    private String contestShowTag;
    private String contestShowComment;
    private Boolean contestPublic;
}
