package com.hust.baseweb.applications.exam.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@FieldNameConstants
public class ExamTestSaveReq {

    private String code;
    private String name;
    private String description;
    private List<ExamTestQuestionSaveReq> examTestQuestionSaveReqList;
    private List<ExamTestQuestionSaveReq> examTestQuestionDeleteReqList;
}
