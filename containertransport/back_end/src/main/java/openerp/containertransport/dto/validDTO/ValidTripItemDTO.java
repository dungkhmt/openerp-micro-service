package openerp.containertransport.dto.validDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ValidTripItemDTO {
    private Boolean check;
    private BigDecimal totalDistant;
    private Long totalTime;
    private String messageErr;
}
