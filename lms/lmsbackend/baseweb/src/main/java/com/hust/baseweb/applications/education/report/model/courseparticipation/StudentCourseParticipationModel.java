package com.hust.baseweb.applications.education.report.model.courseparticipation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourseParticipationModel {

    private String userLoginId;
    private String fullName;
    private String classId;
    private String courseId;
    private String courseName;
    private String eduCourseMaterialName;
    private String createdStamp;
}
