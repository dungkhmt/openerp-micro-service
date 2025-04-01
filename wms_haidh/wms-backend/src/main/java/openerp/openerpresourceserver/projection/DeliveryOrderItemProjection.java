package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface DeliveryOrderItemProjection {
	UUID getAssignedOrderItemId();
	UUID getOrderId();
    LocalDateTime getOrderDate();
    String getProductName();
    BigDecimal getWeight();
    Integer getOriginalQuantity();
    String getBayCode();
    String getLotId();
}
