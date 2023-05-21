package wms.dto.product;

import wms.dto.BaseReturnDTO;
import wms.entity.ProductCategory;
import wms.entity.ProductUnit;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class ProductReturnDTO extends BaseReturnDTO {
    private String code;
    private String name;
    private Integer unitPerBox;
    private ProductUnit productUnit;
    private String brand;
    private ProductCategory productCategory;
    private String status;
    private int massQuantity;
    private String sku;
    double vat;
    double priceBeforeVat;
}
