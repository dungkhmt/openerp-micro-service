package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkinout.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data.MonthAttendance;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MonthAttendanceRequest {
    @NotNull
    private List<String> userIds;
    @NotNull
    private YearMonth month;

    public MonthAttendance toUseCase() {
        return MonthAttendance.builder()
                .month(month)
                .userIds(userIds)
                .build();
    }
}
