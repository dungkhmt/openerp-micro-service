package com.hust.baseweb.applications.education.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddClassModel {

    private String classCode;

    private String userLoginId;

    private String departmentId;

    private String semesterId;

    private String courseId;

    private String classType;

}
