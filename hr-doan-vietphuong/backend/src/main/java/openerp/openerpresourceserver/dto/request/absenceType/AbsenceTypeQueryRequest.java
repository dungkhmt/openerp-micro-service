package openerp.openerpresourceserver.dto.request.absenceType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbsenceTypeQueryRequest {
    private String keyword;
    private Integer type;
    private Integer status;
}
