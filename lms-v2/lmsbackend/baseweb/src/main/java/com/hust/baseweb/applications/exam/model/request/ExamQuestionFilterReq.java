package com.hust.baseweb.applications.exam.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@FieldNameConstants
public class ExamQuestionFilterReq {

    private String code;
    private List<Integer> types;
    private String content;
}
