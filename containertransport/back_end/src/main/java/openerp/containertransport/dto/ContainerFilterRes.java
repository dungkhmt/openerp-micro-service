package openerp.containertransport.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContainerFilterRes {
    private Integer page;
    private Integer pageSize;
    private Long count;
    private List<ContainerModel> containerModels;
}
