package wms.dto.product;

import lombok.Data;

@Data
public class ProductDiscountDTO {
    private double massDiscount;
    private double contractDiscount;
    private String contractTypeCode;
    private String productCode;
    private double priceAfterVat;
}
