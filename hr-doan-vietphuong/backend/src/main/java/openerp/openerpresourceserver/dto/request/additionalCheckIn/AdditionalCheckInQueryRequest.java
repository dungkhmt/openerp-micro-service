package openerp.openerpresourceserver.dto.request.additionalCheckIn;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AdditionalCheckInQueryRequest {
    private Integer status;
    private Integer type;
    private String email;
    private LocalDate from;
    private LocalDate to;
}
