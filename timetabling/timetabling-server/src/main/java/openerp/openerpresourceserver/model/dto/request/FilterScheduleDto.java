package openerp.openerpresourceserver.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterScheduleDto {
    @NotBlank(message = "Classroom is not be null")
    private String classRoom;
    @NotBlank(message = "Study Week is not be null")
    private String studyWeek;
    @NotBlank(message = "Week Day is not be null")
    private String weekDay;
}
