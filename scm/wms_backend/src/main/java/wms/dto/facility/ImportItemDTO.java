package wms.dto.facility;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class ImportItemDTO {
    @NotBlank(message = "Truong product code khong duoc bo trong")
    private String productCode;
    @NotNull(message = "Truong expire date khong duoc bo trong")
    private String expireDate;
    @NotNull(message = "Truong effectQty khong duoc bo trong")
    @Positive(message = "So luong phai lon hon khong")
    private Integer effectQty;
}
