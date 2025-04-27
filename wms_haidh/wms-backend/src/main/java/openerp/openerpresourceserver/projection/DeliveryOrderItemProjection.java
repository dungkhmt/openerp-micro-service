package openerp.openerpresourceserver.projection;

import java.util.UUID;

public interface DeliveryOrderItemProjection {
	UUID getAssignedOrderItemId();
	UUID getOrderId();
    String getProductName();
    double getWeight();
    int getOriginalQuantity();
    String getBayCode();
    String getLotId();
}
