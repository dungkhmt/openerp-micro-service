package openerp.containertransport.dto;

import lombok.Data;

@Data
public class ShipmentFilterRequestDTO {
    private String status;
    private String shipmentCode;
    private Integer page;
    private Integer pageSize;
}
