package com.hust.baseweb.applications.education.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentClassParticipationOutputModel {

    private String date;
    private int count;
    private int accCount;// accumulated

}
