package openerp.openerpresourceserver.dto.request.absenceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AbsenceTypeRequest {
    private Long id;
    @NotNull(message = "Type must not be null")
    private Integer type;
    @NotBlank(message = "Code must not be blank")
    private String code;
    @NotBlank(message = "Description must not be blank")
    private String description;
    @NotNull(message = "HasValue must not be null")
    private Boolean hasValue;
    private Integer status;
}

