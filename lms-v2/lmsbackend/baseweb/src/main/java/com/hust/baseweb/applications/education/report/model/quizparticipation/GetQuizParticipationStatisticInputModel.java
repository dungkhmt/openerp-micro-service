package com.hust.baseweb.applications.education.report.model.quizparticipation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetQuizParticipationStatisticInputModel {

    private String fromDate;
    private String thruDate;
    private int length;
}
