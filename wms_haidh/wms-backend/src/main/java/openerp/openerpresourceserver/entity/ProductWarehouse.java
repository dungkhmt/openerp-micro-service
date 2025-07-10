package openerp.openerpresourceserver.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "wms_product_warehouse")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWarehouse {
    @Id
    private UUID productWarehouseId;
    private UUID productId;
    private UUID warehouseId;
    private double quantityOnHand; // số lượng khả dụng
}
