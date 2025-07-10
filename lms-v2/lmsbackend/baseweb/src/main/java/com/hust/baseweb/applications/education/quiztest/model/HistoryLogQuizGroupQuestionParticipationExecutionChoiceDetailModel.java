package com.hust.baseweb.applications.education.quiztest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryLogQuizGroupQuestionParticipationExecutionChoiceDetailModel {

    private String userLoginId;
    private String fullName;
    private String classCode;
    private String courseId;
    private String courseName;
    private String date;
    private String quizGroupCode;
    private UUID quizGroupId;
    private UUID questionId;
    private UUID choiceAnswerId;

}
