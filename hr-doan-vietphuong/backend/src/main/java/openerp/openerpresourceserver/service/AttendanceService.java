package openerp.openerpresourceserver.service;

import jakarta.servlet.http.HttpServletRequest;
import openerp.openerpresourceserver.dto.response.attendance.AttendanceReportResponse;
import openerp.openerpresourceserver.dto.response.attendance.AttendanceSummaryResponse;
import openerp.openerpresourceserver.entity.Attendance;
import org.bytedeco.opencv.opencv_core.Mat;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    Attendance recordAttendance(Mat face, HttpServletRequest request);

    List<AttendanceReportResponse> getAttendanceReport(LocalDate start, LocalDate end);

    AttendanceSummaryResponse getAttendanceSummary(LocalDate start, LocalDate end);
}