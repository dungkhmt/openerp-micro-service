package openerp.containertransport.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShipmentRes {
    private Integer page;
    private Integer pageSize;
    private Long count;
    private List<ShipmentModel> shipmentModels;
}
