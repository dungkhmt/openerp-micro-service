package wms.dto.product;

import wms.entity.ProductEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class ProductWithPriceDTO {
    @NotBlank(message = "Name khong duoc de trong")
    private String name;
    @Positive(message = "Don vi so luong phai la so duong")
    private Integer unitPerBox;
    private long unitId;
    private String brand;
    private long categoryId;
    private String status;
    @Positive(message = "So luong phai la so duong")
    private int massQuantity;
    private String sku;
    double vat;
    double priceBeforeVat;
}
