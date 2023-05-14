package wms.dto.facility;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ExportFromFacilityDTO {
    @NotBlank(message = "Truong order code khong duoc bo trong")
    private String orderCode;
    @Valid
    List<ExportItemDTO> exportItems;
}
