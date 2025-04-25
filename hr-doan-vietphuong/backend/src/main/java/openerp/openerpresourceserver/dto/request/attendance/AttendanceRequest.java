package openerp.openerpresourceserver.dto.request.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Data
public class AttendanceRequest {
    @NotNull(message = "start must not be null")
    private LocalDate start;
    @NotNull(message = "end must not be null")
    private LocalDate end;
}
