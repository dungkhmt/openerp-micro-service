package openerp.containertransport.dto;

import lombok.Data;

import java.util.List;

@Data
public class TruckFilterRes {
    private Integer page;
    private Integer pageSize;
    private Long count;
    private List<TruckModel> truckModels;
}
