package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data;

import lombok.*;
import lombok.experimental.SuperBuilder;
import openerp.openerpresourceserver.application.port.out.staff.filter.IStaffFilter;
import openerp.openerpresourceserver.constant.StaffStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.IPageableRequest;

@Data
@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class FindStaff implements IStaffFilter, UseCase {
    private String staffCode;
    private String staffName;
    private String staffEmail;
    private StaffStatus status;
    private IPageableRequest pageableRequest;
    private String departmentCode;
    private String jobPositionCode;
}
