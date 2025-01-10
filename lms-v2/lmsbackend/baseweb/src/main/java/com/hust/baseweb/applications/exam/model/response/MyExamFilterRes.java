package com.hust.baseweb.applications.exam.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
@Builder
public class MyExamFilterRes {

    private String examStudentId;
    private String examId;
    private String examCode;
    private String examName;
    private String examDescription;
    private String startTime;
    private String endTime;
    private String examTestId;
    private String examTestCode;
    private String examTestName;
    private String examResultId;
    private Double totalScore;

}
