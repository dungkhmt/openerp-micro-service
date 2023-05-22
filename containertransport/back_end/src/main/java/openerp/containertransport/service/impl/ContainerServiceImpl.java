package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.ContainerFilterRequestDTO;
import openerp.containertransport.dto.ContainerModel;
import openerp.containertransport.dto.FacilityResponsiveDTO;
import openerp.containertransport.dto.TruckModel;
import openerp.containertransport.entity.Container;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.entity.Truck;
import openerp.containertransport.repo.ContainerRepo;
import openerp.containertransport.repo.FacilityRepo;
import openerp.containertransport.service.ContainerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContainerServiceImpl implements ContainerService {
    private final ContainerRepo containerRepo;
    private final FacilityRepo facilityRepo;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;
    @Override
    public ContainerModel createContainer(ContainerModel containerModelDTO) {
        Facility facility = facilityRepo.findById(containerModelDTO.getFacilityId()).get();
        Container container = new Container();
        container.setFacility(facility);
        container.setSize(containerModelDTO.getSize());
        container.setEmpty(containerModelDTO.getIsEmpty());
        container.setStatus("Available");
        container.setCreatedAt(System.currentTimeMillis());
        container.setUpdatedAt(System.currentTimeMillis());
        containerRepo.save(container);
        container.setContainerCode("CONT" + container.getId());
        containerRepo.save(container);
        return convertToModel(container);
    }

    @Override
    public ContainerModel getContainerById(long id) {
        Container container = containerRepo.findById(id);
        ContainerModel containerModel = convertToModel(container);
        return containerModel;
    }

    @Override
    public ContainerModel updateContainer(ContainerModel containerModel) {
        Container container = containerRepo.findById(containerModel.getId());
        if (containerModel.getFacilityId() != null) {
            Facility facility = facilityRepo.findById(containerModel.getFacilityId()).get();
            container.setFacility(facility);
        }
        if (containerModel.getStatus() != null) {
            container.setStatus(containerModel.getStatus());
        }
        if (containerModel.getIsEmpty() != null) {
            container.setEmpty(containerModel.getIsEmpty());
        }
        container.setUpdatedAt(System.currentTimeMillis());
        containerRepo.save(container);
        ContainerModel containerModelUpdate = convertToModel(container);
        return containerModelUpdate;
    }

    @Override
    public List<ContainerModel> filterContainer(ContainerFilterRequestDTO containerFilterRequestDTO) {
        String sql = "SELECT * FROM container_transport_container WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();
        if(containerFilterRequestDTO.getContainerCode() != null) {
            sql += " AND container_code = :containerCode";
            params.put("containerCode", containerFilterRequestDTO.getContainerCode());
        }
        sql += "ORDER BY updated_at DESC";
        Query query = this.entityManager.createNativeQuery(sql, Container.class);
        for (String i : params.keySet()) {
            query.setParameter(i, params.get(i));
        }
        List<Container> truckModels = query.getResultList();
        List<ContainerModel> containerModelList = new ArrayList<>();
        truckModels.forEach((item) -> {
            containerModelList.add(convertToModel(item));
        });
        return containerModelList;
    }

    public ContainerModel convertToModel(Container container) {
        ContainerModel containerModel = modelMapper.map(container, ContainerModel.class);
        FacilityResponsiveDTO facilityResponsiveDTO = new FacilityResponsiveDTO();
        facilityResponsiveDTO.setFacilityId(container.getFacility().getId());
        facilityResponsiveDTO.setFacilityCode(container.getFacility().getFacilityCode());
        facilityResponsiveDTO.setFacilityName(container.getFacility().getFacilityName());
        facilityResponsiveDTO.setLatitude(container.getFacility().getLatitude());
        facilityResponsiveDTO.setLongitude(container.getFacility().getLongitude());
        facilityResponsiveDTO.setAddress(container.getFacility().getAddress());
        containerModel.setFacilityResponsiveDTO(facilityResponsiveDTO);
        return containerModel;
    }
}
