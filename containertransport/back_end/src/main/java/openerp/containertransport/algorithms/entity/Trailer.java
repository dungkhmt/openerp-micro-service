package openerp.containertransport.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Trailer {
    private int trailerID;
    private String trailerCode;
    private int facilityId;
}
