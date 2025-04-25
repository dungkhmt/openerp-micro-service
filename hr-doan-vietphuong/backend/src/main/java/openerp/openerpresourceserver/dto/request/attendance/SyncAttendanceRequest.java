package openerp.openerpresourceserver.dto.request.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SyncAttendanceRequest {
    @NotNull(message = "id must not be null")
    private Integer id;
    @NotNull(message = "time must not be null")
    private LocalDateTime time;
    @NotNull(message = "date must not be null")
    private Integer date;
    @NotBlank(message = "ip must not be blank")
    private String ip;
}
