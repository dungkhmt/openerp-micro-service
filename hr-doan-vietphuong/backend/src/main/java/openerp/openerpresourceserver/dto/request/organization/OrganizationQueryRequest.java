package openerp.openerpresourceserver.dto.request.organization;

import lombok.Data;

@Data
public class OrganizationQueryRequest {
    private String keyword;
    private Integer type;
    private Integer status;
}
