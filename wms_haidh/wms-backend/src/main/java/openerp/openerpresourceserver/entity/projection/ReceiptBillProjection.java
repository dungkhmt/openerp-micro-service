package openerp.openerpresourceserver.entity.projection;

import java.math.BigDecimal;

public interface ReceiptBillProjection {
    String getReceiptBillId();
    String getDescription();
    String getCreatedBy();
    BigDecimal getTotalPrice();
    String getReceiptName();
}

