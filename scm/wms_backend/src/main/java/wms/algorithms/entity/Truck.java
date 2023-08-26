package wms.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Truck {
    private String ID;
    private double capacity;
    private double speed;
    private double transportCostPerUnit;
    private double waitingCost;
}
