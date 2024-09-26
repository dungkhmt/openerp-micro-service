package com.hust.baseweb.applications.education.quiztest.model.quiztestgroup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateQuizTestGroupInputModel {

    private String quizTestId;
    private int numberOfQuizTestGroups;
}
