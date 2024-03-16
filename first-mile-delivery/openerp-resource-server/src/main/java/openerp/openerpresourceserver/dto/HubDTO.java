package openerp.openerpresourceserver.dto;

import lombok.Data;

@Data
public class HubDTO {
    private String hubCode;
    private String address;


    private String longitude;
    private String latitude;
    private String status;

}
