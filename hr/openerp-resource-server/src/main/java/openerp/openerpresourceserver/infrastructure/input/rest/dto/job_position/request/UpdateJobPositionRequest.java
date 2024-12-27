package openerp.openerpresourceserver.infrastructure.input.rest.dto.job_position.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.job_position.usecase_data.UpdateJobPosition;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateJobPositionRequest {
    @NotNull
    private String code;
    private String name;
    private String description;

    public UpdateJobPosition toUseCase(){
        return UpdateJobPosition.builder()
                .code(code)
                .name(name)
                .description(description)
                .build();
    }
}
