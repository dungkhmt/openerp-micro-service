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
@Setter
public class UpdateAbsenceLeaveHours implements UseCase {
    private Float absenceTime;
    private Float absenceTimeUpdated;
    private IStaffFilter staffFilter;

    public Float getLeaveHours() {
        return absenceTimeUpdated - absenceTime;
    }

    public LeaveHoursUpdateType getUpdateType() {
        return LeaveHoursUpdateType.ADDITIONAL;
    }

    public static UpdateAbsenceLeaveHours of(
        Float absenceTime,
        Float absenceTimeUpdated,
        String userId
    ) {
        return UpdateAbsenceLeaveHours.builder()
            .absenceTime(absenceTime)
            .absenceTimeUpdated(absenceTimeUpdated)
            .staffFilter(FindStaff.builder().userLoginId(userId).build())
            .build();
    }
}
