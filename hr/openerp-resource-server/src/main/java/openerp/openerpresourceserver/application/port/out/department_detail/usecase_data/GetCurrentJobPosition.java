package openerp.openerpresourceserver.application.port.out.department_detail.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.ExampleModel;

@Data
@Builder
@Getter
@Setter
public class GetCurrentJobPosition implements UseCase {
    private String userLoginId;
}
