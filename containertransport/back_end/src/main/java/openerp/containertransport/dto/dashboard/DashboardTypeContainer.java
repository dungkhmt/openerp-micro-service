package openerp.containertransport.dto.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class DashboardTypeContainer {
    private Map<Integer, BigDecimal> rateTypeContainer;
}
