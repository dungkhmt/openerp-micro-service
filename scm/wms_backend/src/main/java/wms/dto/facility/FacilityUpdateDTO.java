package wms.dto.facility;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FacilityUpdateDTO {
    private String code;
    private String name;
    private String address;
    private String status;
    private String latitude;
    private String longitude;
}
