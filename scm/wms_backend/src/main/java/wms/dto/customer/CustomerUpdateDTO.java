package wms.dto.customer;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CustomerUpdateDTO {
    private String code;
    private String name;
    private String phone;
    private String address;
    private String status;
    private String facilityCode;
    private String latitude;
    private String longitude;
    private long customerTypeId;
    private String contractTypeCode;
}
