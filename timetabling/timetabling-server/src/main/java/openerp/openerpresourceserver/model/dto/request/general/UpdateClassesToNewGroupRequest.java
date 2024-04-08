package openerp.openerpresourceserver.model.dto.request.general;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateClassesToNewGroupRequest {
    @NotEmpty
    private List<String> ids;
    @NotBlank
    @NotEmpty
    private String groupName;
    @NotBlank
    @NotEmpty
    private String priorityBuilding;

}
