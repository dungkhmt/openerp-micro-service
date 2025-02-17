package openerp.openerpresourceserver.entity.projection;

import java.math.BigDecimal;

public interface SaleOrderItemDetailProjection {
    String getProductName();
    Integer getQuantity();
    BigDecimal getPriceUnit();
    BigDecimal getCompleted();
}
