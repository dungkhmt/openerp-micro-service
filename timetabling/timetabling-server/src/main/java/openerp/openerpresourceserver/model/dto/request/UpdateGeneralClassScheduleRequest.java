package openerp.openerpresourceserver.model.dto.request;

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
    @NotNull
    private String scheduleIndex;
    @Override
    public String toString() {
        return field + " " + value + " " + generalClassId + " " + scheduleIndex;
    }
}
