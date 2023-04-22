package wms.dto.facility;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ImportToFacilityDTO {
    @NotBlank(message = "Truong code khong duoc bo trong")
    private String code;
    @NotBlank(message = "Truong order code khong duoc bo trong")
    private String orderCode;
    List<ImportItemDTO> importItems;
}
