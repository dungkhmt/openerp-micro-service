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
import java.time.YearMonth;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@AutoMapped(target = CheckinoutModel.class)
public class MonthAttendance implements UseCase, IAttendancesFilter {
    private List<String> userIds;
    private YearMonth month;

    @Override
    public LocalDate getStartDate() {
        return this.month.atDay(1);
    }

    @Override
    public LocalDate getEndDate() {
        return this.month.atEndOfMonth();
    }

    @Override
    public boolean sortByDesc() {
        return false;
    }
}
