package com.hust.baseweb.applications.education.quiztest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseAnalyzeDoQuizInClass {
    private String userId;
    private String fullname;
    private int numberSelect;
    private int numberCorrect;
    private int numberCorrectFastest;

}
