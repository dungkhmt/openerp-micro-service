package com.hust.baseweb.applications.exam.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@FieldNameConstants
public class ExamQuestionDeleteReq {

    private String id;
}
