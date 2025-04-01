package openerp.openerpresourceserver.generaltimetabling.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDeleteRequest {

    private Long id;

    @NotBlank(message = "Class Period is required not null")
    private String roomId;
}
