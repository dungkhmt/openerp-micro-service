package openerp.openerpresourceserver.dto.response.employee;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleEmployeeResponse {
    private String email;
    private String fullName;
}
