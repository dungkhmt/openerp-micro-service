package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.model.DepartmentModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.JobPositionModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffDetailModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffModel;

@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StaffDetailResponse extends StaffResponse {
    private Department department;
    private JobPosition jobPosition;

    public static StaffDetailResponse fromModel(StaffDetailModel model) {
        return StaffDetailResponse.builder()
                .staffCode(model.getStaffCode())
                .department(fromModel(model.getDepartment()))
                .jobPosition(fromModel(model.getJobPosition()))
                .userLoginId(model.getUserLoginId())
                .fullname(model.getFullname())
                .status(model.getStatus())
                .dateOfJoin(model.getDateOfJoin())
                .email(model.getEmail())
                .build();
    }

    private static Department fromModel(DepartmentModel model){
        return new Department(model.getDepartmentCode(), model.getDepartmentName());
    }

    private static JobPosition fromModel(JobPositionModel model){
        return new JobPosition(model.getCode(), model.getName());
    }

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Department{
        private String departmentCode;
        private String departmentName;
    }

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class JobPosition{
        private String jobPositionCode;
        private String jobPositionName;
    }
}
