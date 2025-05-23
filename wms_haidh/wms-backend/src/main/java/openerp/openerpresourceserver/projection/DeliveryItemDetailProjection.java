package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryItemDetailProjection {
    
	UUID getId();
	
    String getProductName();

    BigDecimal getWeight();
    
    String getUom();

    Integer getQuantity();

    String getBayCode();

    String getLotId();
}

