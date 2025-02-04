package com.hust.baseweb.applications.exam.model.request;

import com.hust.baseweb.applications.exam.entity.ExamStudentEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@FieldNameConstants
public class ExamSubjectSaveReq {

    private String code;
    private String name;
    private String status;
}
