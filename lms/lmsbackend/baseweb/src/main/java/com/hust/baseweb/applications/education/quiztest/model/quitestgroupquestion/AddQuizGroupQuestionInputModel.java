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
public class AddQuizGroupQuestionInputModel {

    private UUID quizGroupId;
    private UUID questionId;
}
