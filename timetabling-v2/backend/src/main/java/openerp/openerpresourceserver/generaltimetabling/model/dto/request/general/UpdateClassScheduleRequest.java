package openerp.openerpresourceserver.generaltimetabling.model.dto.request.general;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateClassScheduleRequest {
    List<V2UpdateClassScheduleRequest> saveRequests;
}
