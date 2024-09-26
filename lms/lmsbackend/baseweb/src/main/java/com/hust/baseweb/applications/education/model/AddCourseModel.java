package com.hust.baseweb.applications.education.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddCourseModel {
    private String courseId;

    private String courseName;

    private short credit;
}
