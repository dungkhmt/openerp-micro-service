package openerp.openerpresourceserver.generaltimetabling.model.dto.request.general;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGeneralClassScheduleRequest {
    @NotNull
    private String field;
    @NotNull
    private String value;
    @NotNull
    private String generalClassId;
    private Long roomReservationId;
    @Override
    public String toString() {
        return field + " " + value + " " + generalClassId + roomReservationId;
    }
}
