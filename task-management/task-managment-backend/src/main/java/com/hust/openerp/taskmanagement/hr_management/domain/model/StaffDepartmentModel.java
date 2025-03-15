package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffDepartmentModel {
    private DepartmentModel departmentModel;
    private String userLoginId;
    private LocalDate fromDate;
    private LocalDate thruDate;
}
