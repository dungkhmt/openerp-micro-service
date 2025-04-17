package openerp.openerpresourceserver.dto.request.attendanceCycle;

import lombok.Data;
import openerp.openerpresourceserver.enums.AttendanceCycleEnum;

@Data
public class AttendanceCycleRequest {
    private Integer endDay;
    private AttendanceCycleEnum type;
}
