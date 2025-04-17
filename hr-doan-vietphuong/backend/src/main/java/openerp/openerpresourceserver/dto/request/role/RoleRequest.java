package openerp.openerpresourceserver.dto.request.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleRequest {
    private Long id;
    @NotBlank(message = "Privilege Id list must not be null")
    private List<Long> privilegeIdList;
    @NotBlank(message = "Name must not be null")
    private String name;
    private Integer status;
}
