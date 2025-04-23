package openerp.openerpresourceserver.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AttendanceId implements Serializable {
    private Integer id;
    private LocalDateTime time;
}
