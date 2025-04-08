package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ProductInventoryProjection {
	UUID getId();
    String getCode();
    String getName();
    BigDecimal getTotalQuantityOnHand();
    LocalDateTime getDateUpdated();
}

