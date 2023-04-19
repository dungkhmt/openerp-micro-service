package com.hust.baseweb.applications.education.report.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetClassParticipationStatisticInputModel {

    private String fromDate;
    private String thruDate;
    private int length;
}
