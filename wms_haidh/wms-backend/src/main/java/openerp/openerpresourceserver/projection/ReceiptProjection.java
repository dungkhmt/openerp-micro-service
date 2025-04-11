package openerp.openerpresourceserver.projection;

import java.time.LocalDateTime;

public interface ReceiptProjection {
    String getReceiptName();
    String getDescription();
    LocalDateTime getReceiptDate();
    String getWarehouseName();  
    String getCreatedReason();
    LocalDateTime getExpectedReceiptDate();
    String getStatus();
    String getCreatedBy();
    LocalDateTime getCreatedStamp();
}

