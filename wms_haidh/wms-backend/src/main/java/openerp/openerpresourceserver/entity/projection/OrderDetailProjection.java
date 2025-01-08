package openerp.openerpresourceserver.entity.projection;

import java.math.BigDecimal;

public interface OrderDetailProjection {
    String getProductName();
    Integer getQuantity();
    BigDecimal getPriceUnit();
}

