package openerp.openerpresourceserver.dto.response.attendance;

import lombok.Builder;

@Builder
public record AttendanceSummaryResponse(
        Double totalWorkHours,
        Double totalWorkDays,
        Double absenceDayLeft,
        Integer requiredWorkDays,
        Integer totalHolidays) {
}
