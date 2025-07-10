package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.leave_hours.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.leave_hours.usecase_data.UpdateLeaveHours;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.FindStaff;
import com.hust.openerp.taskmanagement.hr_management.constant.LeaveHoursUpdateType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateLeaveHoursRequest {
    private Float leaveHours;
    private LeaveHoursUpdateType updateType;
    private List<String> departmentCodes;
    private List<String> jobPositionCodes;
    private List<String> userIds;

    public UpdateLeaveHours toUseCase(){
        return UpdateLeaveHours.builder()
            .staffFilter(
                FindStaff.builder()
                    .departmentCodes(departmentCodes)
                    .jobPositionCodes(jobPositionCodes)
                    .userLoginIds(userIds)
                    .build()
            )
            .leaveHours(leaveHours)
            .updateType(updateType)
            .build();
    }
}
