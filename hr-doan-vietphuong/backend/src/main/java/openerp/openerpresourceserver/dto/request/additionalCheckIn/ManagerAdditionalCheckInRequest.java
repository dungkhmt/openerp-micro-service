package openerp.openerpresourceserver.dto.request.additionalCheckIn;

import lombok.Data;

import java.util.List;

@Data
public class ManagerAdditionalCheckInRequest {
    private List<Long> idList;
}
