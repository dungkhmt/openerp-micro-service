package openerp.openerpresourceserver.infrastructure.input.rest.dto.department.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.department.usecase_data.CreateDepartment;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateDepartmentRequest {
    @NotNull
    private String departmentName;
    private String description;

    public CreateDepartment toUseCase(){
        return CreateDepartment.builder()
                .departmentName(departmentName)
                .description(description)
                .build();
    }
}
