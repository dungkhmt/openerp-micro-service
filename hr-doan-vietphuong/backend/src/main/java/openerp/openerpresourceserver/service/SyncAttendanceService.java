package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.request.attendance.SyncAttendanceRequest;
import openerp.openerpresourceserver.entity.AttendanceRange;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SyncAttendanceService {
    void syncAttendanceByRange(LocalDate startDate, LocalDate endDate, List<Integer> employeeIds, final Boolean overwrite);
    void syncAttendanceRealTime(SyncAttendanceRequest request);
    Double computeAttendanceTime(LocalTime clockIn, LocalTime clockOut, AttendanceRange attendanceRange);
}
