package openerp.openerpresourceserver.application.port.out.job_position.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.JobPositionModel;

@Data
@Builder
@Getter
@Setter
public class UpdateJobPosition implements UseCase {
    private String code;
    private String name;
    private String description;

    public JobPositionModel toModel() {
        return JobPositionModel.builder()
                .code(code)
                .name(name)
                .description(description)
                .build();
    }
}
