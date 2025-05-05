package openerp.openerpresourceserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobHistory {
    private Long positionId;
    private String positionName;
    private Long organizationId;
    private String organizationName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String createdBy;
}