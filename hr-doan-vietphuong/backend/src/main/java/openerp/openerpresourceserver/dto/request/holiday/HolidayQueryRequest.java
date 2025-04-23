package openerp.openerpresourceserver.dto.request.holiday;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HolidayQueryRequest {
    private LocalDate from;
    private LocalDate to;
    private Integer type;
    private Integer status;
}
