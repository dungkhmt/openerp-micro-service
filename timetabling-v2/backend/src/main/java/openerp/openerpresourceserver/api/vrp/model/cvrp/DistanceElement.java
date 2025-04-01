package openerp.openerpresourceserver.api.vrp.model.cvrp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DistanceElement {
    private String fromPointId;
    private String toPointId;
    private double travelTime;
    private double travelDistance;
}
