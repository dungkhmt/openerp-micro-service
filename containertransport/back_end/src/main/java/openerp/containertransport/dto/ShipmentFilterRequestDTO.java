package openerp.containertransport.dto;

import lombok.Data;

@Data
public class ShipmentFilterRequestDTO {
    private String status;
    private Integer page;
    private Integer pageSize;
}
