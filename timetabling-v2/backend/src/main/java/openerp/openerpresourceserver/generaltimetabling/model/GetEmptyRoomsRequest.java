package openerp.openerpresourceserver.generaltimetabling.model;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetEmptyRoomsRequest {
    private Integer weekDay;
    private Integer startTime;
    private Integer endTime;
    private String crew;
    private Integer week;
}
