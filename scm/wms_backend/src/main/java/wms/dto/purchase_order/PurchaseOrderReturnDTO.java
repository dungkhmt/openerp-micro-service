package wms.dto.purchase_order;

import java.util.List;

public class PurchaseOrderReturnDTO {
    private String orderCode;
    private String supplierCode;
    private String boughtBy;
    private double vat;
    private List<PurchaseOrderItemDTO> orderItems;
}
