package openerp.containertransport.service;

import openerp.containertransport.dto.TypeContainerFilterReqDTO;
import openerp.containertransport.dto.TypeContainerFilterRes;
import openerp.containertransport.dto.TypeContainerModel;
import openerp.containertransport.entity.TypeContainer;

public interface TypeContainerService {
    TypeContainerModel createTypeContainer(TypeContainerModel typeContainerModel);
    TypeContainerFilterRes filterTypeContainer(TypeContainerFilterReqDTO typeContainerModel);
    Float countContainer(Integer size);
}
