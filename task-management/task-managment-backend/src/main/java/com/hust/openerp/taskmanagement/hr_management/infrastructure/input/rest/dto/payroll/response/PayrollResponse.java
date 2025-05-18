package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.payroll.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.PayrollStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PayrollResponse {
    private UUID id;
    private String name;
    private Integer totalWorkDays;
    private Float workHoursPerDay;
    private Integer totalHolidayDays;
    private LocalDate fromDate;
    private LocalDate thruDate;
    private String createdBy;
    private PayrollStatus status;

    public static PayrollResponse fromModel(PayrollModel model) {
        return PayrollResponse.builder()
            .id(model.getId())
            .name(model.getName())
            .totalWorkDays(model.getTotalWorkDays())
            .workHoursPerDay(model.getWorkHoursPerDay())
            .totalHolidayDays(model.getTotalHolidayDays())
            .createdBy(model.getCreatedBy())
            .status(model.getStatus())
            .thruDate(model.getThruDate())
            .fromDate(model.getFromDate())
            .build();
    }
}

