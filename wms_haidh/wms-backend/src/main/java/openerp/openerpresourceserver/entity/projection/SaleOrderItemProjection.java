package openerp.openerpresourceserver.entity.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface SaleOrderItemProjection {
    UUID getSaleOrderItemId();
    String getProductName();
    Integer getQuantity();
    BigDecimal getPriceUnit();
    BigDecimal getCompleted();
}
