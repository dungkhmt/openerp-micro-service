package openerp.openerpresourceserver.dto.response.organization;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleOrganizationResponse {
    private Long id;
    private String name;
}
