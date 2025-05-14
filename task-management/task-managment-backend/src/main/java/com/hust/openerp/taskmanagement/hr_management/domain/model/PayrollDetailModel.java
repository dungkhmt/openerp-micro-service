package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.SalaryType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
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
    private List<Double> workHours;
    private List<Double> absenceHours;
    private Float totalWorkHours;
    private Float pairLeaveHours;
    private Float unpairLeaveHours;
    private Integer payrollAmount;
}
