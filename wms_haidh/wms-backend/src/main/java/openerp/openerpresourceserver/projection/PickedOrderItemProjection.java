package openerp.openerpresourceserver.projection;

import java.util.UUID;

public interface PickedOrderItemProjection {
	UUID getAssignedOrderItemId();
    String getProductName();
    int getOriginalQuantity();
    String getBayCode();
    String getLotId();
    String getStatus();
}
