package openerp.openerpresourceserver.labtimetabling.entity.autoscheduling;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
public class AutoSchedulingRequest {
    HashMap<Long, Integer> weekConstraintMap;
    HashMap<Long, Integer> avoidWeekMap;
}
