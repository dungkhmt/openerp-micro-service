package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;

public interface DeliveryTripProjection {

    String getDeliveryTripId();

    String getDeliveryPersonName();
    
    String getWarehouseName();

    BigDecimal getDistance();

    int getTotalLocations();

    String getStatus();

}
