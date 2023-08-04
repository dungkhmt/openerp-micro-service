package openerp.containertransport.dto.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardUseTruck {
    private List<RateUseEntityDTO> rateUseTruck;
}
