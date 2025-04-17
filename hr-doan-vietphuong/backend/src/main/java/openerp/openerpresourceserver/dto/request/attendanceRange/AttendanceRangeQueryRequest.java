package openerp.openerpresourceserver.dto.request.attendanceRange;

import lombok.Data;

@Data
public class AttendanceRangeQueryRequest {
    private String keyword;
    private Integer status;
}
