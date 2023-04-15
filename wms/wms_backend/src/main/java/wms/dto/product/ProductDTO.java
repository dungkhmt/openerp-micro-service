package wms.dto.product;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {
    @NotBlank(message = "Ma code khong duoc de trong")
    private String code;
    private String name;
    private Long unitQuantity;
    private Boolean status;
    private String color;
    private Long massNumber;
    private BigDecimal sku;
    private String image;
    @Positive(message = "So luong phai la so duong")
    private Long quantity;
}
