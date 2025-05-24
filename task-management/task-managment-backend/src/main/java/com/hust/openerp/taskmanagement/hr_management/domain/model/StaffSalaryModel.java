package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.SalaryType;
import lombok.*;

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
