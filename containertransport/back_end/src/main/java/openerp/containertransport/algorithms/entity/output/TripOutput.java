package openerp.containertransport.algorithms.entity.output;

import lombok.Data;
import openerp.containertransport.algorithms.entity.Point;
import openerp.containertransport.algorithms.entity.Request;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class TripOutput implements Serializable {
    private Integer truckId;
    private List<Request> requests;
    private BigDecimal totalTime;
    private BigDecimal totalTimeTmp;
    private BigDecimal totalDistant;
    private BigDecimal totalDistantTmp;
    private List<Point> points;
}
