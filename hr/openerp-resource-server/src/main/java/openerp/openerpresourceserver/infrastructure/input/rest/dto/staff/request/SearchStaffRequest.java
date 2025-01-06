package openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.staff.usecase_data.FindStaff;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.PageableRequest;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchStaffRequest {
    private String fullName;
    private String staffCode;
    private String staffEmail;
    private PageableRequest pageableRequest;

    public FindStaff toUseCase(){
        return FindStaff.builder()
                .staffEmail(staffEmail)
                .staffCode(staffCode)
                .staffName(fullName)
                .pageableRequest(pageableRequest)
                .build();
    }
}
