package openerp.openerpresourceserver.application.port.out.job_position.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.DepartmentStatus;
import openerp.openerpresourceserver.constant.JobPositionStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.DepartmentModel;
import openerp.openerpresourceserver.domain.model.JobPositionModel;

@Data
@Builder
@Getter
@Setter
public class CreateJobPosition implements UseCase {
    private String name;
    private String description;

    public JobPositionModel toModel() {
        return JobPositionModel.builder()
                .name(name)
                .description(description)
                .status(JobPositionStatus.ACTIVE)
                .build();
    }
}
