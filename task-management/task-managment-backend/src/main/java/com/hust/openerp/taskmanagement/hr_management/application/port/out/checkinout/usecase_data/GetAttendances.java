package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.filter.IAttendancesFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckinoutModel;
import com.hust.openerp.taskmanagement.hr_management.processor.AutoMapped;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@AutoMapped(target = CheckinoutModel.class)
public class GetAttendances implements UseCase, IAttendancesFilter {
    private List<String> userIds;
    private LocalDate startDate;
    private LocalDate endDate;
    @Override
    public boolean sortByDesc() {
        return false;
    }
}
