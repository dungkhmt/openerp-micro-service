package com.hust.baseweb.applications.exam.model.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
public class ExamTestQuestionFilterRes {

    private String id;
    private String examTestId;
    private String examQuestionId;
    private Integer order;
}
