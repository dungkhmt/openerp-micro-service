package openerp.containertransport.dto.dashboard;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RateUseEntityDTO {
    private String name;
    private BigDecimal rate;
}
