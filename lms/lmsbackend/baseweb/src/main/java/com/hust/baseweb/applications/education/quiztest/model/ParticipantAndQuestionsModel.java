package com.hust.baseweb.applications.education.quiztest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantAndQuestionsModel {
    private String participantUserLoginId;
    private String fullName;
    private QuizGroupTestDetailModel quizGroupTestDetailModel;
}
