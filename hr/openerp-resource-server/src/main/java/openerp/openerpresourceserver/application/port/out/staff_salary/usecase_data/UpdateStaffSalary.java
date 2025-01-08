package openerp.openerpresourceserver.application.port.out.staff_salary.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.SalaryType;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.StaffSalaryModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class UpdateStaffSalary implements UseCase {
    private String userLoginId;
    private Integer salary;
    private SalaryType salaryType;

    public StaffSalaryModel toModel(){
        return StaffSalaryModel.builder()
                .userLoginId(getUserLoginId())
                .salary(salary)
                .fromDate(LocalDateTime.now())
                .salaryType(salaryType == null ? SalaryType.getDefaultValue() : salaryType)
                .build();
    }
}
