package openerp.openerpresourceserver.projection;

import java.util.UUID;

public interface PutawayItemProjection {
	UUID getReceiptItemId();
    String getProductName();
    int getQuantity();
    String getLotId();
    String getBayCode();
    String getStatus();
}

