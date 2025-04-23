package openerp.openerpresourceserver.repo.projection;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceDTO {
    private Integer id;

    private Integer date;

    private LocalDateTime clockIn;

    private LocalDateTime clockOut;

    private Long attendanceRangeId;
}
