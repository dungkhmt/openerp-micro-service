package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderProjection {
    String getOrderId();
    LocalDateTime getOrderDate();
    BigDecimal getTotalOrderCost();
    String getCustomerName();
    String getStatus();
    String getApprovedBy();
    String getCancelledBy();
}

