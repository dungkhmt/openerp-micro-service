package openerp.openerpresourceserver.generaltimetabling.model.dto.request.general;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGeneralClassRequest {
    @NotNull
    private String field;

    @NotNull
    private String generalClassId;

    @NotNull
    private String value;
}
