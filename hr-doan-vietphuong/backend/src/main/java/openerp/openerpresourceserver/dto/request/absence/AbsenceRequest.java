package openerp.openerpresourceserver.dto.request.absence;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AbsenceRequest {
    private Long id;
    @NotNull(message = "Absence Type Id must not be null")
    private Long absenceTypeId;
    @NotNull(message = "Start time must not be null 0 - Morning, 1 - Afternoon")
    private Integer startTime;
    @NotNull(message = "Start Date must not be null")
    private LocalDate startDate;
    @NotNull(message = "End time must not be null 0 - Morning, 1 - Afternoon")
    private Integer endTime;
    @NotNull(message = "End Date must not be null")
    private LocalDate endDate;
    private String note;
    private List<Long> organizationIdList;
    private List<String> ccList;
}