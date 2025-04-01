package openerp.openerpresourceserver.generaltimetabling.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriorityGroupUpdateDto {
    private Long id;

    @NotBlank(message = "Group name is required not null")
    private String groupName;

    private String oldRoomName;

    private String roomName;

    private Integer priority;
}
