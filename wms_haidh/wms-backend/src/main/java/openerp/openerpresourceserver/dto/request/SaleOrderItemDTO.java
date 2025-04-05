package openerp.openerpresourceserver.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleOrderItemDTO {
    private UUID productId;
    private Integer quantity;
    private BigDecimal priceUnit;
}

