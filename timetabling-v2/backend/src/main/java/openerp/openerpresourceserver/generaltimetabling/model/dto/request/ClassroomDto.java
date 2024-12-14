package openerp.openerpresourceserver.generaltimetabling.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassroomDto {

    private Long id;

    @NotBlank(message = "Classroom is required not null")
    private String classroom;

    @NotBlank(message = "Building is required not null")
    private String building;

    @NotBlank(message = "Quantity max is required not null")
    private String quantityMax;

    private String description;
}
