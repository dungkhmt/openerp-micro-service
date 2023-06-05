package openerp.containertransport.algorithms.entity.output;

import lombok.*;
import openerp.containertransport.algorithms.entity.Request;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportContainerSolutionOutput {
    private Map<Integer, TripOutput> tripOutputs;
    private Map<Integer, TripOutput> tripOutputsTmp;
    private BigDecimal totalTime;
    private BigDecimal totalDistant;
    private BigDecimal totalTimeTmp;
    private BigDecimal totalDistantTmp;
    private long startTime;
}
