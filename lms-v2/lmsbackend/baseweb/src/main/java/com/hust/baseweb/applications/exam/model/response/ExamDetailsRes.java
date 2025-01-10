package com.hust.baseweb.applications.exam.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
public class ExamDetailsRes {

    private String examTestId;
    private List<ExamTestDetailsRes> examTests;
    private String id;
    private String code;
    private String name;
    private String description;
    private Integer status;
    private String startTime;
    private String endTime;
    private List<ExamStudentResultDetailsRes> examStudents;
}
