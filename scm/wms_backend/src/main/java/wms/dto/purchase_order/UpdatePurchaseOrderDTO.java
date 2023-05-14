package wms.dto.purchase_order;

import lombok.Data;

import java.util.List;

@Data
public class UpdatePurchaseOrderDTO {
    private String createdOrderCode;
    private double vat;
    private List<PurchaseOrderItemDTO> orderItems;
}
