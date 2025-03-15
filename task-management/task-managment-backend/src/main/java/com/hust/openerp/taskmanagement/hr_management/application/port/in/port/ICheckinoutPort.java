package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.filter.IAttendancesFilter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.filter.ICheckinoutFilter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data.Checkinout;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckinoutModel;

import java.util.List;

public interface ICheckinoutPort {
    void checkinout(Checkinout checkinout);
    List<CheckinoutModel> getCheckinout(ICheckinoutFilter filter);
    List<CheckinoutModel> getCheckinout(IAttendancesFilter filter);
}
