package openerp.openerpresourceserver.generaltimetabling.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateGeneralClass {
    private GeneralClass generalClass;
}
