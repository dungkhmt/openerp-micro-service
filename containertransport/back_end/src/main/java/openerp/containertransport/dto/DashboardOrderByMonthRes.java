package openerp.containertransport.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class DashboardOrderByMonthRes {
    private Map<String, BigDecimal> orderByMonth;
}
