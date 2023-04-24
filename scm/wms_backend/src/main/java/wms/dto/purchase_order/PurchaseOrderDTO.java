package wms.dto.purchase_order;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderDTO {
    private String supplierCode;
    private String boughtBy;
    private double vat;
    private List<PurchaseOrderItemDTO> orderItems;
}
