package com.hust.baseweb.applications.exam.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@FieldNameConstants
@Builder
public class MyExamDetailsRes {

    private String examStudentId;
    private String examId;
    private String examAnswerStatus;
    private String examCode;
    private String examName;
    private String examDescription;
    private String startTime;
    private String endTime;
    private String examTestId;
    private String examTestCode;
    private String examTestName;
    private String examResultId;
    private Integer totalScore;
    private Integer totalTime;
    private String submitedAt;
    private String answerFiles;
    private String comment;
    private List<MyExamQuestionDetailsRes> questionList;

}
