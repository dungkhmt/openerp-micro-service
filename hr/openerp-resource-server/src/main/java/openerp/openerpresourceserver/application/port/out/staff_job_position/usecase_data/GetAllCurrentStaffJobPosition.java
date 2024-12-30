package openerp.openerpresourceserver.application.port.out.staff_job_position.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.common.model.UseCase;

import java.util.List;

@Data
@Builder
@Getter
@Setter
public class GetAllCurrentStaffJobPosition implements UseCase {
    private List<String> userLoginIds;
}
