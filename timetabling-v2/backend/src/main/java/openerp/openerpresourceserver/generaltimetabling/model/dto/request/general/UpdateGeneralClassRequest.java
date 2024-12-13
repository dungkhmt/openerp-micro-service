package openerp.openerpresourceserver.generaltimetabling.model.dto.request.general;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;


@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGeneralClassRequest {
    private GeneralClass generalClass;
}
