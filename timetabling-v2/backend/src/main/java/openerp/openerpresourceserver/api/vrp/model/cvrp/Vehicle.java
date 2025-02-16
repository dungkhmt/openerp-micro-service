package openerp.openerpresourceserver.api.vrp.model.cvrp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    private String id;
    private double capacity;
    private Point depot;
}
