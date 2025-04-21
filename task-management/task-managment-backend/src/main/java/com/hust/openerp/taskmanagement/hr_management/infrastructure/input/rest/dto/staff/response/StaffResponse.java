package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffModel;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StaffResponse {
    private String staffCode;
    private String userLoginId;
    private String fullname;
    private StaffStatus status;
    private String email;
    private LocalDate dateOfJoin;
    private Float leaveHours;

    public static StaffResponse fromModel(StaffModel model) {
        return StaffResponse.builder()
            .staffCode(model.getStaffCode())
            .userLoginId(model.getUserLoginId())
            .fullname(model.getFullname())
            .status(model.getStatus())
            .email(model.getEmail())
            .dateOfJoin(model.getDateOfJoin())
            .leaveHours(model.getLeaveHours())
            .build();
    }
}
