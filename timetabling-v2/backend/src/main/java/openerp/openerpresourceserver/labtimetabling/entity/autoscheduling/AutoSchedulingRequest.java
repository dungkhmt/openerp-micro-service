package openerp.openerpresourceserver.labtimetabling.entity.autoscheduling;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
public class AutoSchedulingRequest {
    Long solvingTimeLimit;
}
