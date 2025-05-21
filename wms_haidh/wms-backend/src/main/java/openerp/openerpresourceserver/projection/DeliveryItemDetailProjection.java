package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;

public interface DeliveryItemDetailProjection {
    
	String getId();
	
    String getProductName();

    BigDecimal getWeight();
    
    String getUom();

    Integer getQuantity();

    String getBayCode();

    String getLotId();
}

