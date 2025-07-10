package openerp.openerpresourceserver.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssignOrdersRequest {
    private List<UUID> orderIds;
    // getter and setter
}