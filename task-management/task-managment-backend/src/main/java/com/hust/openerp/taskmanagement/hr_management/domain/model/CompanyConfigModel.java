package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
public class CompanyConfigModel {
    private LocalTime startWorkTime;
    private LocalTime endWorkTime;
    private LocalTime startLunchTime;
    private LocalTime endLunchTime;
    private Float hourBeforeAnnounceAbsence;

    public Float getTotalWorkTime(){
        long totalWorkMinutes = java.time.Duration.between(startWorkTime, endWorkTime).toMinutes();
        long lunchBreakMinutes = java.time.Duration.between(startLunchTime, endLunchTime).toMinutes();
        long netWorkMinutes = totalWorkMinutes - lunchBreakMinutes;
        return netWorkMinutes / 60f;
    }
}
