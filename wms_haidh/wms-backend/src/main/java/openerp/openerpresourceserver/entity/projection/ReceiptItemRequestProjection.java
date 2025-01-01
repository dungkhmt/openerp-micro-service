package openerp.openerpresourceserver.entity.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ReceiptItemRequestProjection {
	UUID getReceiptItemRequestId();

	int getQuantity();

	BigDecimal getCompleted();

	LocalDateTime getExpectedReceiptDate();

	String getWarehouseName();

	String getProductName();
}