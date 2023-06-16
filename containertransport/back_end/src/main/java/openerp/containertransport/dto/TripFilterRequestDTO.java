package openerp.containertransport.dto;

import lombok.Data;

@Data
public class TripFilterRequestDTO {
    private Long shipmentId;
    private String status;
    private String username;
}
