package openerp.openerpresourceserver.projection;

import java.time.LocalDateTime;

public interface InventoryItemProjection {
	String getInventoryItemId();
    String getProductName();          // Tên sản phẩm
    String getBayCode();              // Mã bay
    String getLotId();                // Lot ID
    int getQuantityOnHandTotal();     // Số lượng tồn kho
    LocalDateTime getLastUpdatedStamp(); // Thời gian cập nhật cuối
}



