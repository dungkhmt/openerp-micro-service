package wms.dto.facility;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FacilityDTO {
    @NotBlank(message = "Truong code khong duoc bo trong")
    private String code;
    @NotBlank(message = "Truong name khong duoc bo trong")
    private String name;
    @NotBlank(message = "Dia chi khong duoc de trong")
    private String address;
    private String status;
    private String latitude;
    private String longitude;
    private String createdBy;
    private String managedBy;
}
