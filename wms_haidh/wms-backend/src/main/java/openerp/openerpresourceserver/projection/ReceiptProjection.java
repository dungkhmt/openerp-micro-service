package openerp.openerpresourceserver.projection;

import java.time.LocalDateTime;

public interface ReceiptProjection {
    String getReceiptName();
    String getDescription();
    String getWarehouseName(); 
    String getSupplierName();
    LocalDateTime getExpectedReceiptDate();
    String getStatus();
    String getCreatedBy();
    LocalDateTime getCreatedStamp();
}

