package com.hust.baseweb.applications.education.report.model.quizparticipation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizParticipationStatisticOutputModel {

    private String date;
    private int count;
    private int accCount;
}
