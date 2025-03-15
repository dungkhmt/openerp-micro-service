package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff_job_position.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffJobPositionModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.job_position.response.JobPositionResponse;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StaffJobPositionResponse {
    private JobPositionResponse jobPosition;
    private String userLoginId;
    private LocalDate fromDate;
    private LocalDate thruDate;

    public static StaffJobPositionResponse fromModel(StaffJobPositionModel model) {
        return StaffJobPositionResponse.builder()
                .jobPosition(JobPositionResponse.fromModel(model.getJobPosition()))
                .userLoginId(model.getUserLoginId())
                .fromDate(model.getFromDate())
                .thruDate(model.getThruDate())
                .build();
    }
}
