package openerp.openerpresourceserver.entity.projection;

import java.util.UUID;

public interface CustomerDeliveryProjection {
    
    UUID getOrderId();

    String getCustomerName();

    String getCustomerPhoneNumber();

    String getCustomerAddress();

    int getSequence();
}

