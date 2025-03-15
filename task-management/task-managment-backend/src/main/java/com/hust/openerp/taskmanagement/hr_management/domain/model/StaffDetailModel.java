package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class StaffDetailModel {
    private String staffCode;
    private String userLoginId;
    private String fullname;
    private StaffStatus status;
    private String email;
    private LocalDate dateOfJoin;
    private DepartmentModel department;
    private JobPositionModel jobPosition;

    public static StaffDetailModel of(
            StaffModel staffModel,
            DepartmentModel departmentModel,
            JobPositionModel jobPositionModel
    ){
        return StaffDetailModel.builder()
                .staffCode(staffModel.getStaffCode())
                .userLoginId(staffModel.getUserLoginId())
                .fullname(staffModel.getFullname())
                .status(staffModel.getStatus())
                .dateOfJoin(staffModel.getDateOfJoin())
                .email(staffModel.getEmail())
                .department(departmentModel)
                .jobPosition(jobPositionModel)
                .build();
    }
}
