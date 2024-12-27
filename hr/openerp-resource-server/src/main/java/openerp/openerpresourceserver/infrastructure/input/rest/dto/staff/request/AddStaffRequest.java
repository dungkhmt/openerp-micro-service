package openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.staff.usecase_data.AddStaff;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddStaffRequest {
    @NotNull
    @Size(min = 1, max = 50)
    private String fullName;
    @NotNull
    private String userLoginId;

    public AddStaff toUseCase(){
        return AddStaff.builder()
                .fullName(fullName)
                .userLoginId(userLoginId)
                .build();
    }
}
