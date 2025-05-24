package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.DepartmentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DepartmentModel {
    private String departmentCode;
    private String departmentName;
    private String description;
    private DepartmentStatus status;
}
