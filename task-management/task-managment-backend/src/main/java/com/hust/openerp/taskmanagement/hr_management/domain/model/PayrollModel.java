package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.PayrollStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PayrollModel {
    private UUID id;
    private String name;
    private Integer totalWorkDays;
    private Float workHoursPerDay;
    private Integer totalHolidayDays;
    private LocalDate fromDate;
    private LocalDate thruDate;
    private String createdBy;
    private PayrollStatus status;
}
