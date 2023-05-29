package openerp.containertransport.dto;

import lombok.Data;

import java.util.List;

@Data
public class TrailerFilterRes {
    private Integer page;
    private Integer pageSize;
    private Long count;
    private List<TrailerModel> trailerModels;
}
