package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.SalaryType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class PayrollDetailModel {
    private UUID id;
    private UUID payrollId;
    private String userId;
    private Integer salary;
    private SalaryType salaryType;
    private Boolean isPaidHoliday;
    private Float workHours;
    private Float pairLeaveHours;
    private Float unpairLeaveHours;
    private Integer payrollAmount;
}
