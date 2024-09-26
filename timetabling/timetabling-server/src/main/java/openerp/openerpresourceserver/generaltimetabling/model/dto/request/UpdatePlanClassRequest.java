package openerp.openerpresourceserver.generaltimetabling.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.PlanGeneralClass;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlanClassRequest {
    private PlanGeneralClass planClass;
}
