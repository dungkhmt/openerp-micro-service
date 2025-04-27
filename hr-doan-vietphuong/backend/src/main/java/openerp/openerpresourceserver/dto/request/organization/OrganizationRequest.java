package openerp.openerpresourceserver.dto.request.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrganizationRequest {
    private Long id;
    private Long parentId;
    @NotBlank(message = "Organization name must not be empty")
    private String name;
    private Integer type;
    @NotNull(message = "Lead must not be null")
    private Long leadId;
    private Integer status;
}

