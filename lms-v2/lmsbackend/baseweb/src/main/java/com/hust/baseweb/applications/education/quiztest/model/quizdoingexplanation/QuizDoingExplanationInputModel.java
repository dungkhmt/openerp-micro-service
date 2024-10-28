package com.hust.baseweb.applications.education.quiztest.model.quizdoingexplanation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class QuizDoingExplanationInputModel {

    private UUID questionId;

    private String participantUserId;

    private String testId;

    private String solutionExplanation;

}
