package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;

public interface SaleOrderItemDetailProjection {
    String getProductName();
    String getAddressName();
    Integer getQuantity();
    String getUom();
    BigDecimal getPriceUnit();
    BigDecimal getCompleted();
}
