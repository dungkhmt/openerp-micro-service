package openerp.openerpresourceserver.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutoMakeScheduleDto {
    @NotBlank(message = "Semester is null")
    private String semester;

    @NotBlank(message = "Group Name is null")
    private String groupName;

    @NotBlank(message = "Weekday Priority is null")
    private String weekdayPriority;

    private Boolean isClassroomArranged;
}
