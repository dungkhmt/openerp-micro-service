package openerp.openerpresourceserver.dto.request.holiday;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HolidayQueryRequest {
    private LocalDate from;
    private LocalDate to;
    private Integer type;
    private Integer status;
}
