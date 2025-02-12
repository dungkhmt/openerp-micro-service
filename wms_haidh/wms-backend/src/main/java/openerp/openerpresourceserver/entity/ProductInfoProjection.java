package openerp.openerpresourceserver.entity;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductInfoProjection {
    UUID getId();
    String getCode();
    String getName();
    BigDecimal getTotalQuantityOnHand();
}

