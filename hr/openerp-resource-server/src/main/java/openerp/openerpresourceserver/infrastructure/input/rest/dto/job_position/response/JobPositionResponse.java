package openerp.openerpresourceserver.infrastructure.input.rest.dto.job_position.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.DepartmentStatus;
import openerp.openerpresourceserver.domain.model.DepartmentModel;
import openerp.openerpresourceserver.domain.model.JobPositionModel;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JobPositionResponse {
    private String code;
    private String name;
    private String description;

    public static JobPositionResponse fromModel(JobPositionModel model) {
        return JobPositionResponse.builder()
                .code(model.getCode())
                .name(model.getName())
                .description(model.getDescription())
                .build();
    }
}
