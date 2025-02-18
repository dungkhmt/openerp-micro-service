package com.hust.baseweb.applications.exam.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@FieldNameConstants
public class MyExamResultSaveReq {

    private String examId;
    private String examStudentId;
    private Integer totalTime;
    private List<MyExamResultDetailsSaveReq> examResultDetails;
}
