package com.hust.baseweb.applications.education.quiztest.model.quitestgroupquestion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizGroupQuestionDetailOutputModel {

    private UUID quizGroupId;
    private String groupCode;
    private String questionStatement;
    private UUID questionId;
}
