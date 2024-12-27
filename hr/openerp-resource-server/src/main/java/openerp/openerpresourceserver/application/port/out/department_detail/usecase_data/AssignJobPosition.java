package openerp.openerpresourceserver.application.port.out.department_detail.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.common.model.UseCase;

@Data
@Builder
@Getter
@Setter
public class AssignJobPosition implements UseCase {
    private String userLoginId;
    private String jobPositionCode;
}
