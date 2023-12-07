package openerp.openerpresourceserver.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassPeriodDto {

    private Long id;

    @NotBlank(message = "Class Period is required not null")
    private String classPeriod;
}
