package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductPriceProjection {
	UUID getProductId();
    String getName();
    BigDecimal getPrice();
}

