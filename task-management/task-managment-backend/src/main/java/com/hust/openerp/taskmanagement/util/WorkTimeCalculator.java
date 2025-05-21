package com.hust.openerp.taskmanagement.util;

import com.hust.openerp.taskmanagement.hr_management.domain.model.CompanyConfigModel;

import java.time.Duration;
import java.time.LocalTime;

public class WorkTimeCalculator {
    public static Float calculateWorkTimeByHours(
        LocalTime startTime,
        LocalTime endTime,
        CompanyConfigModel companyConfig
    ) {
        if(startTime == null || endTime == null) return 0f;
        if (endTime.isBefore(startTime)) {

            return 0f;
        }

        Duration totalDuration = Duration.between(startTime, endTime);

        LocalTime lunchStart = companyConfig.getStartLunchTime();
        LocalTime lunchEnd = companyConfig.getEndLunchTime();

        LocalTime workStart = startTime.isBefore(lunchStart) ? lunchStart : startTime;
        LocalTime workEnd = endTime.isAfter(lunchEnd) ? lunchEnd : endTime;

        Duration overlapLunch = Duration.ZERO;
        if (!workEnd.isBefore(workStart)) {
            overlapLunch = Duration.between(workStart, workEnd);
        }

        Duration actualWorkTime = totalDuration.minus(overlapLunch);

        return actualWorkTime.toMinutes() / 60f;
    }
}
