package openerp.openerpresourceserver.generaltimetabling.model.dto.request.general;

import lombok.*;
import openerp.openerpresourceserver.generaltimetabling.model.dto.MakeGeneralClassRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkMakeGeneralClassRequest {
    private MakeGeneralClassRequest classRequest;
    private int quantity;
    private String classType;
}
