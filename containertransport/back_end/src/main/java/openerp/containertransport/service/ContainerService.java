package openerp.containertransport.service;

import openerp.containertransport.dto.ContainerFilterRequestDTO;
import openerp.containertransport.dto.ContainerFilterRes;
import openerp.containertransport.dto.ContainerModel;
import openerp.containertransport.entity.Container;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ContainerService {
    ResponseEntity<?> createContainer(ContainerModel containerModel, String username);
    ContainerModel getContainerByUid (String uid);
    ContainerModel updateContainer(ContainerModel containerModel);
    ContainerModel deleteContainer(String uid);
    ContainerFilterRes filterContainer(ContainerFilterRequestDTO containerFilterRequestDTO);
}
