package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductProjection {
    UUID getProductId();
    String getName();
    String getImageUrl();
    BigDecimal getPrice();
    BigDecimal getQuantity();
}
