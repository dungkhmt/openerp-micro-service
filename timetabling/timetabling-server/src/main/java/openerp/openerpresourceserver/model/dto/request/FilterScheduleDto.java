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
    private String classCode;

    @NotBlank(message = "Classroom is not be null")
    private String classRoom;

    private String classType;

    private String institute;

    private String managementCode;

    private String moduleCode;

    private String openBatch;

    @NotBlank(message = "Semester is not be null")
    private String semester;

    private String state;

    private String studyTime;

    @NotBlank(message = "Study Week is not be null")
    private String studyWeek;

    private String weekDay;
}
