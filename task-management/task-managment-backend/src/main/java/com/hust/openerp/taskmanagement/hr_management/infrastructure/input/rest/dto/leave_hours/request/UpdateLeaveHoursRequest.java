package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.leave_hours.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.config.usecase_data.UpdateConfigs;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.leave_hours.usecase_data.UpdateLeaveHours;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigKey;
import com.hust.openerp.taskmanagement.hr_management.constant.LeaveHoursUpdateType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateLeaveHoursRequest {
    private Float leaveHours;
    private LeaveHoursUpdateType updateType;

    public UpdateLeaveHours toUseCase(){
        return UpdateLeaveHours.builder()
            .leaveHours(leaveHours)
            .updateType(updateType)
            .build();
    }
}
