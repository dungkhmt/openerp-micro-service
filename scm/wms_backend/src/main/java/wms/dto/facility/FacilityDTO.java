package wms.dto.facility;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FacilityDTO {
    @NotBlank(message = "Truong name khong duoc bo trong")
    private String name;
    @NotBlank(message = "Dia chi khong duoc de trong")
    private String address;
    private String latitude;
    private String longitude;
    private String managedBy;
}
