package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;

public interface ReceiptBillProjection {
    String getReceiptBillId();
    String getDescription();
    String getCreatedBy();
    BigDecimal getTotalPrice();
    String getReceiptName();
}

