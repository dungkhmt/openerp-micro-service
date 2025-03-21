package openerp.openerpresourceserver.entity.projection;

import java.math.BigDecimal;

public interface TodayDeliveryTripProjection {

	String getDeliveryTripId();

	BigDecimal getDistance();

	int getTotalLocations();

	String getStatus();

	String getDescription();
}
