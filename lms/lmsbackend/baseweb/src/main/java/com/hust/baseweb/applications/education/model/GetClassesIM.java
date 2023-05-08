package com.hust.baseweb.applications.education.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetClassesIM {

    private Integer code;

    private String classCode;

    private String courseId;

    private String courseName;

    private String classType;

    private String departmentId;
}
