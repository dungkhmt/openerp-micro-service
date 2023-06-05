package openerp.containertransport.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class DistantKey {
    private int fromFacility;
    private int toFacility;
}
