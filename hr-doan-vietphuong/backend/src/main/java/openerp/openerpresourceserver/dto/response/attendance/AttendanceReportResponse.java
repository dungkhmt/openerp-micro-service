package openerp.openerpresourceserver.dto.response.attendance;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
public record AttendanceReportResponse(LocalTime startTime,
                                       LocalTime endTime,
                                       Double attendanceTime,
                                       String note,
                                       Double bonusTime,
                                       LocalDate date,
                                       String attendanceStatus,
                                       LocalDateTime updatedAt) {
}