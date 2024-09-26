package openerp.containertransport.algorithms.entity.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DistantAndTimeRequest {
    private long distantRequest;
    private long timeRequest;
}
