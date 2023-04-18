package openerp.containertransport.service;

import openerp.containertransport.dto.ContainerFilterRequestDTO;
import openerp.containertransport.dto.ContainerModel;
import openerp.containertransport.entity.Container;

import java.util.List;

public interface ContainerService {
    ContainerModel createContainer(ContainerModel containerModel);
    ContainerModel getContainerById (long id);
    ContainerModel updateContainer(ContainerModel containerModel);
    List<ContainerModel> filterContainer(ContainerFilterRequestDTO containerFilterRequestDTO);
}
