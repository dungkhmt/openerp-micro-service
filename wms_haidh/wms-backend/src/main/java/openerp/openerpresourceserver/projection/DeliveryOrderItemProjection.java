package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryOrderItemProjection {
	UUID getAssignedOrderItemId();
	UUID getOrderId();
    String getProductName();
    BigDecimal getWeight();
    Integer getOriginalQuantity();
    String getBayCode();
    String getLotId();
}
