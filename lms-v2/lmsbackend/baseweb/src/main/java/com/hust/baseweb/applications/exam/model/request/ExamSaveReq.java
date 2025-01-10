package com.hust.baseweb.applications.exam.model.request;

import com.hust.baseweb.applications.exam.entity.ExamStudentEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldNameConstants
public class ExamSaveReq {

    private String examTestId;
    private String code;
    private String name;
    private String description;
    private Integer status;
    private String startTime;
    private String endTime;
    private List<ExamStudentEntity> examStudents;
    private List<ExamStudentEntity> examStudentDeletes;
}
