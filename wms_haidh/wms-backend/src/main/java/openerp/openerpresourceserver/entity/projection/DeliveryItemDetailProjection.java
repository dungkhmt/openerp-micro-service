package openerp.openerpresourceserver.entity.projection;

import java.math.BigDecimal;

public interface DeliveryItemDetailProjection {
    
    String getProductName();

    BigDecimal getWeight();

    Integer getQuantity();

    String getBayCode();

    String getLotId();
}

