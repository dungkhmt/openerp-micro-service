package openerp.openerpresourceserver.generaltimetabling.model.dto.request.general;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupNameRequest {
    private Long id;
    private String groupName;
}
