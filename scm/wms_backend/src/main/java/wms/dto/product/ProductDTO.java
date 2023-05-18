package wms.dto.product;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {
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
    // TODO: Add images
//    @Positive(message = "So luong phai la so duong")
//    private Long quantity;
}
