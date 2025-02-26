package com.hust.baseweb.applications.exam.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
public class ExamMarkingDetailsSaveReq {

    private String id;
    private String examResultId;
    private String examQuestionId;
    private String answer;
    private String filePath;
    private Integer score;
}
