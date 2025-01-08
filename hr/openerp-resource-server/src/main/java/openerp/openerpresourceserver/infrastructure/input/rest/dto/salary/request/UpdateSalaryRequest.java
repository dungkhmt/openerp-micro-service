package openerp.openerpresourceserver.infrastructure.input.rest.dto.salary.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.staff_salary.usecase_data.UpdateStaffSalary;
import openerp.openerpresourceserver.constant.SalaryType;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateSalaryRequest {
    @NotNull
    private String userLoginId;
    private Integer salary;
    private SalaryType salaryType;

    public UpdateStaffSalary toUseCase(){
        return UpdateStaffSalary.builder()
                .userLoginId(userLoginId)
                .salary(salary)
                .salaryType(salaryType)
                .build();
    }
}
