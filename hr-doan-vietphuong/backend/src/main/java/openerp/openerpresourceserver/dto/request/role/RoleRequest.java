package openerp.openerpresourceserver.dto.request.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RoleRequest {
    private Long id;
    @NotBlank(message = "Privilege Id list must not be null")
    private List<Long> privilegeIdList;
    @NotBlank(message = "Name must not be null")
    private String name;
    private Integer status;
}
