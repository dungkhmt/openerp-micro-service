package openerp.openerpresourceserver.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ReceiptInfoProjection {
    UUID getReceiptId();
    String getReceiptName();
    String getWarehouseName();
    LocalDateTime getExpectedReceiptDate();
    String getStatus();
    String getCreatedBy();
    String getApprovedBy();
    String getCancelledBy();
}

