package openerp.openerpresourceserver.entity.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface SaleOrderItemProjection {
    UUID getSaleOrderItemId();
    LocalDateTime getOrderDate();
    String getCustomerName();
    String getProductName();
    Integer getQuantity();
    BigDecimal getPriceUnit();
    BigDecimal getCompleted();
}
