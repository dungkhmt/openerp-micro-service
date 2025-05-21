package openerp.openerpresourceserver.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface ReceiptItemRequestDetailProjection {
	UUID getReceiptItemRequestId();

	int getQuantity();

	BigDecimal getCompleted();

	String getProductName();
	
	String getUom();
	
	String getWarehouseName();
}
