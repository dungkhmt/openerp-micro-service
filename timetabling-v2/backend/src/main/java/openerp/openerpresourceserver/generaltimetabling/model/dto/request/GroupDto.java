package openerp.openerpresourceserver.generaltimetabling.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDto {

    private Long id;

    @NotBlank(message = "Group name is required not null")
    private String groupName;

    private String roomName;

    private Integer priority;
}
