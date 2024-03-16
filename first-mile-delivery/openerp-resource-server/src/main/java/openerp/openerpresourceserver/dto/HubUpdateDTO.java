package openerp.openerpresourceserver.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class HubUpdateDTO {

    private UUID id;
    private String hubCode;
    private String address;


    private String longitude;
    private String latitude;
    private String status;

}