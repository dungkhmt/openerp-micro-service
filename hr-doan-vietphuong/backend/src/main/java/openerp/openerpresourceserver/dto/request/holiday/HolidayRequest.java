package openerp.openerpresourceserver.dto.request.holiday;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HolidayRequest {
    private Long id;
    @NotNull(message = "Date must not be null")
    private LocalDate date;
    @NotNull(message = "Type must not be null")
    private Integer type;
    @NotNull(message = "Note must not be null")
    private String note;
    private Double bonusTime = 0.0;
    @NotNull(message = "Status must not be null")
    private Integer status;
}
