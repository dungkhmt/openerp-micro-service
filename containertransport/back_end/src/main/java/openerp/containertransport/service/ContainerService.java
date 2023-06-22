package openerp.containertransport.service;

import openerp.containertransport.dto.ContainerFilterRequestDTO;
import openerp.containertransport.dto.ContainerFilterRes;
import openerp.containertransport.dto.ContainerModel;
import openerp.containertransport.entity.Container;

import java.util.List;

public interface ContainerService {
    ContainerModel createContainer(ContainerModel containerModel);
    ContainerModel getContainerByUid (String uid);
    ContainerModel updateContainer(ContainerModel containerModel);
    ContainerFilterRes filterContainer(ContainerFilterRequestDTO containerFilterRequestDTO);
}
