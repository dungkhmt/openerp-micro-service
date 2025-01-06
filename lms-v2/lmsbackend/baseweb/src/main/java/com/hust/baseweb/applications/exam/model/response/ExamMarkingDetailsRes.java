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
public class ExamMarkingDetailsRes {

    private String examId;
    private String examTestId;
    private String examStudentId;
    private String examStudentCode;
    private String examStudentName;
    private String examStudentEmail;
    private String examStudentPhone;
    private String examResultId;
    private Integer totalScore;
    private Integer totalTime;
    private String submitedAt;
    private String answerFiles;
    private String comment;
    private List<MyExamQuestionDetailsRes> questionList;

}
