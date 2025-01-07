package openerp.openerpresourceserver.application.port.out.staff_job_position.usecase_data;

import lombok.*;
import openerp.openerpresourceserver.domain.common.model.UseCase;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCurrentJobPosition implements UseCase {
    private String userLoginId;
}
