package openerp.openerpresourceserver.domain.model;

import lombok.*;
import openerp.openerpresourceserver.constant.SalaryType;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffSalaryModel {
    private String userLoginId;
    private LocalDate fromDate;
    private LocalDate thruDate;
    private Integer salary;
    private SalaryType salaryType;
}
