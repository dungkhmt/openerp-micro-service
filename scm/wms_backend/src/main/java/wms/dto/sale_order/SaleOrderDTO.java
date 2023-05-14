package wms.dto.sale_order;

import lombok.Data;
import wms.dto.purchase_order.PurchaseOrderItemDTO;
import wms.entity.SaleOrderItem;

import java.util.List;

@Data
public class SaleOrderDTO {
    private String boughtBy; // customer
    private Double discount;
    private List<SaleOrderItemDTO> orderItems;
}
