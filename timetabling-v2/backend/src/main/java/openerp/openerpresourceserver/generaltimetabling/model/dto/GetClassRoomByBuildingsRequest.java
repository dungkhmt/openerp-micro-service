package openerp.openerpresourceserver.generaltimetabling.model.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
public class GetClassRoomByBuildingsRequest {
    private String groupName;
    private Integer maxAmount;
}
