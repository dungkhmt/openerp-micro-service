package com.hust.baseweb.applications.education.model.quiz;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizQuestionTagCreateModel {

    private UUID questionId;

    private UUID tagId;
}
