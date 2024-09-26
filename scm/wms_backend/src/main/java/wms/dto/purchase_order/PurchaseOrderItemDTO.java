package wms.dto.purchase_order;

import lombok.Data;
import wms.entity.ProductEntity;

@Data
public class PurchaseOrderItemDTO {
    private String productCode;
    private Integer quantity;
    private Double priceUnit;
}
