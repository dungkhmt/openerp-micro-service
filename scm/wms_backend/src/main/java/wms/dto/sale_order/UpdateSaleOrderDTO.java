package wms.dto.sale_order;

import lombok.Data;
import wms.dto.purchase_order.PurchaseOrderItemDTO;

import java.util.List;

@Data
public class UpdateSaleOrderDTO {
    private String boughtBy;
    private String createdOrderCode;
    private double discount;
    private List<SaleOrderItemDTO> orderItems;
}
