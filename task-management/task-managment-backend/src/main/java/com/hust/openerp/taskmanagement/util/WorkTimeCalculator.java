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
        if (startTime == null || endTime == null || companyConfig == null) {
            return 0f;
        }
        if (endTime.isBefore(startTime)) {
            return 0f;
        }

        LocalTime companyWorkStart = companyConfig.getStartWorkTime();
        LocalTime companyWorkEnd = companyConfig.getEndWorkTime();
        LocalTime companyLunchStart = companyConfig.getStartLunchTime();
        LocalTime companyLunchEnd = companyConfig.getEndLunchTime();

        LocalTime effectiveStartTime = startTime.isBefore(companyWorkStart) ? companyWorkStart : startTime;
        LocalTime effectiveEndTime = endTime.isAfter(companyWorkEnd) ? companyWorkEnd : endTime;

        if (effectiveEndTime.isBefore(effectiveStartTime)) {
            return 0f;
        }

        Duration grossWorkDuration = Duration.between(effectiveStartTime, effectiveEndTime);

        LocalTime overlapLunchStart = effectiveStartTime.isAfter(companyLunchStart) ? effectiveStartTime : companyLunchStart;
        LocalTime overlapLunchEnd = effectiveEndTime.isBefore(companyLunchEnd) ? effectiveEndTime : companyLunchEnd;

        Duration lunchDeduction = Duration.ZERO;
        if (overlapLunchEnd.isAfter(overlapLunchStart)) {
            lunchDeduction = Duration.between(overlapLunchStart, overlapLunchEnd);
        }

        Duration actualWorkTime = grossWorkDuration.minus(lunchDeduction);

        return actualWorkTime.toMinutes() / 60.0f;
    }
}