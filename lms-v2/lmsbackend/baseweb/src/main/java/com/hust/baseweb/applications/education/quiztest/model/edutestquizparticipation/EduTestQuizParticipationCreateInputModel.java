package com.hust.baseweb.applications.education.quiztest.model.edutestquizparticipation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EduTestQuizParticipationCreateInputModel {

    private String testQuizId;
    //private String userLoginId; take this param from principal of Controller
}
