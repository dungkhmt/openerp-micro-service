package openerp.openerpresourceserver.application.port.out.staff_salary.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.common.model.UseCase;

@Data
@Builder
@Getter
@Setter
public class GetCurrentStaffSalary implements UseCase {
    private String userLoginId;
}
