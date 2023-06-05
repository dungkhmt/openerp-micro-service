package openerp.containertransport.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DistanceElement {
    private int fromFacility;
    private int toFacility;
    private BigDecimal distance; // in meters
    private long travelTime;// in seconds
}
