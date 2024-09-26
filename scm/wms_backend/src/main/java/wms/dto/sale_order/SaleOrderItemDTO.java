package wms.dto.sale_order;

import lombok.Data;

@Data
public class SaleOrderItemDTO {
    private String productCode;
    private Integer quantity;
    private Double priceUnit;
}
