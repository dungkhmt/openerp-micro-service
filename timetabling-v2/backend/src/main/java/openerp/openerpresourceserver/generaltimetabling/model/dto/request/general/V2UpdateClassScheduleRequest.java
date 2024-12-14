package openerp.openerpresourceserver.generaltimetabling.model.dto.request.general;

import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class V2UpdateClassScheduleRequest {
    @NonNull
    private Long roomReservationId;
    private Integer startTime;
    private Integer endTime;
    private Integer weekday;
    private String room;
}
