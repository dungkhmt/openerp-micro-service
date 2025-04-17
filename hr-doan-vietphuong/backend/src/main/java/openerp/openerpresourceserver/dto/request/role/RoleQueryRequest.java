package openerp.openerpresourceserver.dto.request.role;

import lombok.Data;

@Data
public class RoleQueryRequest {
    private String keyword;
    private Integer status;
}
