package openerp.openerpresourceserver.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MakeScheduleDto {

    @NotNull
    private Long id;

    private String startPeriod;

    private String weekday;

    private String classroom;

    private String secondStartPeriod;

    private String secondWeekday;

    private String secondClassroom;

    private Boolean isSeparateClass;
}
