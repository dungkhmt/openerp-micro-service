package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkinout.filter.IAttendancesFilter;
import openerp.openerpresourceserver.application.port.out.checkinout.filter.ICheckinoutFilter;
import openerp.openerpresourceserver.constant.CheckinoutType;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.CheckinoutModel;
import openerp.openerpresourceserver.processor.AutoMapped;

import java.time.LocalDate;
import java.time.MonthDay;
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
