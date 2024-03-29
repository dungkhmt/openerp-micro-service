package openerp.openerpresourceserver.dto;

import lombok.Data;

@Data
public class CustomerDTO {
    private String customerName;
    private String address;
    private String phone;
    private String email;
    private String latitude;
    private String longitude;
    private String status;
    private String note;
}
