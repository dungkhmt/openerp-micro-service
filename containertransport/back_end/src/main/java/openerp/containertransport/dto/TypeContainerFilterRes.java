package openerp.containertransport.dto;

import lombok.Data;
import openerp.containertransport.entity.TypeContainer;

import java.util.List;

@Data
public class TypeContainerFilterRes {
    private Integer page;
    private Integer pageSize;
    private Long count;
    private List<TypeContainerModel> typeContainers;
}
