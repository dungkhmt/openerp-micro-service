package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.DepartmentStatus;

@Getter
@Setter
@Entity
@Table(name = "hr_department")
public class DepartmentEntity extends AuditEntity{
    @Id
    @Column(name = "department_code", nullable = false, length = 100)
    private String departmentCode;

    @Column(name = "department_name", length = 200)
    private String departmentName;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "status", length = 100)
    @Enumerated(EnumType.STRING)
    private DepartmentStatus status;

}