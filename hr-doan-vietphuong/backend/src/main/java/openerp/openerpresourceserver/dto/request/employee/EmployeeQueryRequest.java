package openerp.openerpresourceserver.dto.request.employee;

import lombok.Data;

@Data
public class EmployeeQueryRequest {
    private String keyword;
    private Integer status;
    private Long roleId;
}
