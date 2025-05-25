package openerp.openerpresourceserver.projection;

import java.time.LocalDateTime;

public interface InventoryItemProjection {
	String getInventoryItemId();
    String getProductName();          // Tên sản phẩm
    String getLotId();                // Lot ID
    int getAvailableQuantity(); // Số lượng tồn kho khả dụng
    int getQuantityOnHandTotal();     // Số lượng tồn kho thật sự
    LocalDateTime getLastUpdatedStamp(); // Thời gian cập nhật cuối
}



