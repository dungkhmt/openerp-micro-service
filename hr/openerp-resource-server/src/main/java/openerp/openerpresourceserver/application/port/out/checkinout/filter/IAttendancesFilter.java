package openerp.openerpresourceserver.application.port.out.checkinout.filter;

import openerp.openerpresourceserver.constant.CheckinoutType;

import java.time.LocalDate;
import java.util.List;

public interface IAttendancesFilter {
    List<String> getUserIds();
    LocalDate getStartDate();
    LocalDate getEndDate();
    boolean sortByDesc();
}
