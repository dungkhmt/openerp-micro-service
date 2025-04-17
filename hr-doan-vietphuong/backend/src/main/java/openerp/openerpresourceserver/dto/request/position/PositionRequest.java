package openerp.openerpresourceserver.dto.request.position;

import lombok.Data;

import java.util.List;

@Data
public class PositionRequest {
    private Long id;
    private String name;
    private String description;
    private Integer status;
    private Boolean isOfficial;
    private Boolean isLead;
    private List<Long> roleIdList;
}