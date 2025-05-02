package openerp.openerpresourceserver.dto.request.absence;

import lombok.Data;

import java.util.List;

@Data
public class ManagerAbsenceRequest {
    private List<Long> idList;
}