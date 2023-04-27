package wms.dto.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProductCategoryDTO {
    @NotBlank(message = "Truong name khong duoc bo trong")
    private String name;
}
