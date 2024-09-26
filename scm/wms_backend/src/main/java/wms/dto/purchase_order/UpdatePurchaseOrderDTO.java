package wms.dto.purchase_order;

import lombok.Data;

import java.util.List;

@Data
public class UpdatePurchaseOrderDTO {
    private String boughtBy;
    private String createdOrderCode;
    private double vat;
    private String supplierCode;
    private List<PurchaseOrderItemDTO> orderItems;
}
