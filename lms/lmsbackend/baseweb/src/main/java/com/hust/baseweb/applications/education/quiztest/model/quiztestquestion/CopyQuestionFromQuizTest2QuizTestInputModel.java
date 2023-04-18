package com.hust.baseweb.applications.education.quiztest.model.quiztestquestion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CopyQuestionFromQuizTest2QuizTestInputModel {
    private String fromTestId;
    private String toTestId;
}
