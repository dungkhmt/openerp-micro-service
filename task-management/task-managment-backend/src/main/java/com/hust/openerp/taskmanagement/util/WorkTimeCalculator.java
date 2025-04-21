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
        if (endTime.isBefore(startTime)) {
            // Nếu kết thúc trước khi bắt đầu thì không hợp lệ
            return 0f;
        }

        // Tổng thời gian làm việc
        Duration totalDuration = Duration.between(startTime, endTime);

        // Xác định thời gian nghỉ trưa
        LocalTime lunchStart = companyConfig.getStartLunchTime();
        LocalTime lunchEnd = companyConfig.getEndLunchTime();

        // Tính thời gian overlap giữa thời gian làm việc và thời gian nghỉ trưa
        LocalTime workStart = startTime.isBefore(lunchStart) ? lunchStart : startTime;
        LocalTime workEnd = endTime.isAfter(lunchEnd) ? lunchEnd : endTime;

        Duration overlapLunch = Duration.ZERO;
        if (!workEnd.isBefore(workStart)) {
            overlapLunch = Duration.between(workStart, workEnd);
        }

        // Trừ thời gian nghỉ trưa ra khỏi tổng thời gian làm việc
        Duration actualWorkTime = totalDuration.minus(overlapLunch);

        // Trả về số giờ dưới dạng float
        return actualWorkTime.toMinutes() / 60f;
    }
}
