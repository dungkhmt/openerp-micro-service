package openerp.openerpresourceserver.dto.response.absence;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AbsenceResponse {
    private Long id;
    private String email;
    private String startTime;
    private LocalDate startDate;
    private String endTime;
    private LocalDate endDate;
    private Double length;
    private Integer type;
    private String subType;
    private String note;
    private Integer status;
    private Integer emailStatus;
}