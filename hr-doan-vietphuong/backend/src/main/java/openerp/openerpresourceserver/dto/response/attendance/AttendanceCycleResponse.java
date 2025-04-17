package openerp.openerpresourceserver.dto.response.attendance;
import openerp.openerpresourceserver.enums.AttendanceCycleEnum;

import java.time.LocalDate;

public record AttendanceCycleResponse(LocalDate startDate, LocalDate endDate, AttendanceCycleEnum type) {
}
