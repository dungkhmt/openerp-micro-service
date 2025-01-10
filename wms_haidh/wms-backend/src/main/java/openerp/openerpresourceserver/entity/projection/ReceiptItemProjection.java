package openerp.openerpresourceserver.entity.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ReceiptItemProjection {
    int getQuantity();
    String getBayCode();
    BigDecimal getImportPrice();
    LocalDateTime getExpiredDate();
    String getLotId();
    String getReceiptBillId();
}

