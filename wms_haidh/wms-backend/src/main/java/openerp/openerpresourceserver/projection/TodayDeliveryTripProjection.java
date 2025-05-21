package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;

public interface TodayDeliveryTripProjection {

	String getDeliveryTripId();
	
	String getWarehouseName();

	BigDecimal getDistance();

	int getTotalLocations();

	String getStatus();

}
