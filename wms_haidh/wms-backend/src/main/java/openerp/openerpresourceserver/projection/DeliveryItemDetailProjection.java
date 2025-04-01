package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;

public interface DeliveryItemDetailProjection {
    
	String getId();
	
    String getProductName();

    BigDecimal getWeight();

    Integer getQuantity();

    String getBayCode();

    String getLotId();
}

