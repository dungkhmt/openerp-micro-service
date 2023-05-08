package com.hust.baseweb.applications.education.quiztest.model.quiztestquestion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuizTestQuestionInputModel {
    private String testId;
    private UUID questionId;

}
