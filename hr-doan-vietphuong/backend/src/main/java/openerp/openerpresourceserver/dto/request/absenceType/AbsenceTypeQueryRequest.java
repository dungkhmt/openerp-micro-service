package openerp.openerpresourceserver.dto.request.absenceType;

import lombok.Data;

@Data
public class AbsenceTypeQueryRequest {
    private String keyword;
    private Integer type;
    private Integer status;
}
