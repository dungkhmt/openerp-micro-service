package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.salary.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.SalaryType;
import openerp.openerpresourceserver.domain.model.StaffSalaryModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StaffSalaryResponse {
    private String userLoginId;
    private LocalDateTime fromDate;
    private LocalDateTime thruDate;
    private Integer salary;
    private SalaryType salaryType;

    public static StaffSalaryResponse fromModel(StaffSalaryModel model) {
        if (model == null) return null;
        return StaffSalaryResponse.builder()
                .userLoginId(model.getUserLoginId())
                .fromDate(model.getFromDate())
                .salary(model.getSalary())
                .salaryType(model.getSalaryType())
                .thruDate(model.getThruDate())
                .build();
    }
}
