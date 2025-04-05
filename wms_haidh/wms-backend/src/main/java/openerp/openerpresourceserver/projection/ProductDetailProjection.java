package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductDetailProjection {
    UUID getProductId();
    String getCode();
    String getName();
    String getDescription();
    BigDecimal getHeight();
    BigDecimal getWeight();
    BigDecimal getArea();
    String getUom();
    String getImageUrl(); 
    BigDecimal getPrice(); 
}

