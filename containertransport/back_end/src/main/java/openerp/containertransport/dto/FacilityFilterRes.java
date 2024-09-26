package openerp.containertransport.dto;

import lombok.Data;

import java.util.List;

@Data
public class FacilityFilterRes {
    private Integer page;
    private Integer pageSize;
    private Long count;
    private List<FacilityModel> facilityModels;
}
