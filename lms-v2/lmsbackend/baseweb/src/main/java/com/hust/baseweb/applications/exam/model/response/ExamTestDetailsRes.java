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
public class ExamTestDetailsRes {

    private String id;
    private String code;
    private String name;
    private String description;
    private List<ExamTestQuestionDetailsRes> examTestQuestionDetails;
}
