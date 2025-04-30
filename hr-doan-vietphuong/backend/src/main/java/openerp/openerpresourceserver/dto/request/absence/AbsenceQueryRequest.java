package openerp.openerpresourceserver.dto.request.absence;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AbsenceQueryRequest {
    private Integer status;
    private Integer type;
    private String email;
    private LocalDate from;
    private LocalDate to;
}

