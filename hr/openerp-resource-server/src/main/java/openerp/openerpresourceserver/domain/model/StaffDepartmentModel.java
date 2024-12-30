package openerp.openerpresourceserver.domain.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffDepartmentModel {
    private String departmentCode;
    private String userLoginId;
    private LocalDate fromDate;
    private LocalDate thruDate;
}
