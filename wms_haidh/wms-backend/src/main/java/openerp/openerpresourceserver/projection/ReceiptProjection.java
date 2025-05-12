package openerp.openerpresourceserver.projection;

import java.time.LocalDateTime;

public interface ReceiptProjection {
    String getReceiptName();
    String getDescription();
    String getWarehouseName();  
    LocalDateTime getExpectedReceiptDate();
    String getStatus();
    String getCreatedBy();
    LocalDateTime getCreatedStamp();
}

