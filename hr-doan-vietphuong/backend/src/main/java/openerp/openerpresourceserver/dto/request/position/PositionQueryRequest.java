package openerp.openerpresourceserver.dto.request.position;

import lombok.Data;

@Data
public class PositionQueryRequest {
    private String keyword;
    private Integer status;
}
