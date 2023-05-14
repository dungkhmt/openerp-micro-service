package wms.dto.category;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ProductUnitDTO {
    @NotBlank(message = "Truong name khong duoc bo trong")
    private String name;
}
