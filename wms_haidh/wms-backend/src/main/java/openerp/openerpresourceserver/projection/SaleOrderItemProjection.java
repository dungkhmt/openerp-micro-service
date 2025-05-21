package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface SaleOrderItemProjection {
    UUID getSaleOrderItemId();
    String getProductName();
    Integer getQuantity();
    String getUom();
    BigDecimal getPriceUnit();
    BigDecimal getCompleted();
}
