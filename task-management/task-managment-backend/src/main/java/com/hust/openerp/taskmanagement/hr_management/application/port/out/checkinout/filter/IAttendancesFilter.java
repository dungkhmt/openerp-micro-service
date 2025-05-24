package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.filter;

import java.time.LocalDate;
import java.util.List;

public interface IAttendancesFilter {
    List<String> getUserIds();
    LocalDate getStartDate();
    LocalDate getEndDate();
    boolean sortByDesc();
}
