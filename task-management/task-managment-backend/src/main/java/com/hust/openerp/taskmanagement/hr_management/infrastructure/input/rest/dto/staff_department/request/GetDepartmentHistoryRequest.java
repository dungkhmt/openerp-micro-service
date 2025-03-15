package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff_department.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.staff_department.usecase_data.StaffDepartmentHistory;
import openerp.openerpresourceserver.application.port.out.staff_job_position.usecase_data.StaffJobPositionHistory;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetDepartmentHistoryRequest {
    @NotNull
    private String userLoginId;

    public StaffDepartmentHistory toUseCase(){
        return StaffDepartmentHistory.builder()
                .userLoginId(userLoginId)
                .build();
    }
}
