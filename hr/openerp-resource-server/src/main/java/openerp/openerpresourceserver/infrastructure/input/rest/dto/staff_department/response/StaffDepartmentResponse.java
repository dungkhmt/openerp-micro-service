package openerp.openerpresourceserver.infrastructure.input.rest.dto.staff_department.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.model.StaffDepartmentModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.department.response.DepartmentResponse;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StaffDepartmentResponse {
    private DepartmentResponse departmentModel;
    private String userLoginId;
    private LocalDate fromDate;
    private LocalDate thruDate;

    public static StaffDepartmentResponse fromModel(StaffDepartmentModel model) {
        return StaffDepartmentResponse.builder()
                .departmentModel(DepartmentResponse.fromModel(model.getDepartmentModel()))
                .userLoginId(model.getUserLoginId())
                .fromDate(model.getFromDate())
                .thruDate(model.getThruDate())
                .build();
    }
}
