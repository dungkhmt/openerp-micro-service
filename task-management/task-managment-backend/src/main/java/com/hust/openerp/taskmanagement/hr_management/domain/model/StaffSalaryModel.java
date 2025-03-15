package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.*;
import openerp.openerpresourceserver.constant.SalaryType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffSalaryModel {
    private String userLoginId;
    private LocalDateTime fromDate;
    private LocalDateTime thruDate;
    private Integer salary;
    private SalaryType salaryType;
}
