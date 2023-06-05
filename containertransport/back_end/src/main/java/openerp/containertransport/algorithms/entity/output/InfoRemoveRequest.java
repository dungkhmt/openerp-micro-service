package openerp.containertransport.algorithms.entity.output;

import lombok.*;
import openerp.containertransport.algorithms.entity.Request;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoRemoveRequest {
    private int requestId;
    private int truckId;
}
