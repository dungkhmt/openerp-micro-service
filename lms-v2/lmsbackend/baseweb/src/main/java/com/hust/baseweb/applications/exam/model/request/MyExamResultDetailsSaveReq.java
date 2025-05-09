package com.hust.baseweb.applications.exam.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
public class MyExamResultDetailsSaveReq {

    private String examResultId;
    private String examQuestionId;
    private Integer questionOrder;
    private String filePath;
    private String answer;
}
