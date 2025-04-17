package openerp.openerpresourceserver.dto.response.position;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PositionResponse {
    private Long id;
    private String name;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private List<Long> roleIdList;
    private Boolean isLead;
    private Boolean isOfficial;
}