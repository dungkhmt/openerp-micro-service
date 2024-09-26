package com.hust.baseweb.applications.education.quiztest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EduQuizTestModel {

    private String testId;

    private String testName;

    private String scheduleDatetime;

    private String courseId;

    private String statusId;

    private String viewTypeId;
}
