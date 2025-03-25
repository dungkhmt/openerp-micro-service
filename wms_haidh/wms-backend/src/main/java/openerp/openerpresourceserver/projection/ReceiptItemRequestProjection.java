package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface ReceiptItemRequestProjection {
	UUID getReceiptItemRequestId();

	int getQuantity();

	BigDecimal getCompleted();

	String getWarehouseName();

	String getProductName();
}