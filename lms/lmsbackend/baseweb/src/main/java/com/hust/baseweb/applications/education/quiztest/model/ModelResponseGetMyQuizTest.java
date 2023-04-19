package com.hust.baseweb.applications.education.quiztest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ModelResponseGetMyQuizTest {
    private String testId;
    private String testName;
    private String viewTypeId;
    private String statusId;
}
