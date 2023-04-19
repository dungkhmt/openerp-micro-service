package wms.dto.customer;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class CustomerDTO {
    @NotBlank(message = "Ma code khong duoc de trong")
    private String code;
    @NotBlank(message = "Name khong duoc de trong")
    private String name;
    @NotBlank(message = "So dien thoai khong duoc de trong")
    private String phone;
    @NotBlank(message = "Dia chi khong duoc de trong")
    private String address;
    private String status;
    @NotBlank(message = "Ma kho hang khong duoc de trong")
    private String facilityCode;
    private String latitude;
    private String longitude;
    private String createdBy;
    private long customerTypeId;
    private String contractTypeCode;
}
