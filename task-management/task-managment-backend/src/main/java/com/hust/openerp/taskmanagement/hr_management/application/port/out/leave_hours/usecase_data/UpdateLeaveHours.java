package com.hust.openerp.taskmanagement.hr_management.application.port.out.leave_hours.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.filter.IStaffFilter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.FindStaff;
import com.hust.openerp.taskmanagement.hr_management.constant.LeaveHoursUpdateType;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class UpdateLeaveHours implements UseCase {
    private Float leaveHours;
    private LeaveHoursUpdateType updateType;
    private IStaffFilter staffFilter;

    public static UpdateLeaveHours of(
        Float leaveHours,
        LeaveHoursUpdateType updateType,
        String userId) {
        return UpdateLeaveHours.builder()
            .leaveHours(leaveHours)
            .updateType(updateType)
            .staffFilter(FindStaff.builder().userLoginId(userId).build())
            .build();
    }
}
