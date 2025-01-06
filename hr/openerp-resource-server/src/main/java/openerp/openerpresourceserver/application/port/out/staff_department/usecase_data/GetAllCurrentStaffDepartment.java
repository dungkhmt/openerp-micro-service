package openerp.openerpresourceserver.application.port.out.staff_department.usecase_data;

import lombok.*;
import openerp.openerpresourceserver.domain.common.model.UseCase;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllCurrentStaffDepartment implements UseCase {
    private List<String> userLoginIds;
}
