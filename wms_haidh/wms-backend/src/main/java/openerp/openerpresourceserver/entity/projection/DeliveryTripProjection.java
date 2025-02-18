package openerp.openerpresourceserver.entity.projection;

import java.math.BigDecimal;

public interface DeliveryTripProjection {

    String getDeliveryTripId();

    String getDeliveryPersonName();

    BigDecimal getDistance();

    int getTotalLocations();

    String getStatus();

    String getDescription();
}
