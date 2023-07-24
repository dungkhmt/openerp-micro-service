package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.Constants;
import openerp.containertransport.dto.*;
import openerp.containertransport.entity.Container;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.entity.Truck;
import openerp.containertransport.entity.TypeContainer;
import openerp.containertransport.repo.ContainerRepo;
import openerp.containertransport.repo.FacilityRepo;
import openerp.containertransport.repo.TypeContainerRepo;
import openerp.containertransport.service.ContainerService;
import openerp.containertransport.utils.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
    private final TypeContainerRepo typeContainerRepo;
    @Override
    public ContainerModel createContainer(ContainerModel containerModelDTO, String username) {
        TypeContainer typeContainer = typeContainerRepo.findByTypeContainerCode(containerModelDTO.getTypeContainerCode());
        typeContainer.setTotal(typeContainer.getTotal() + 1);

        Facility facility = facilityRepo.findById(containerModelDTO.getFacilityId()).get();
        Container container = new Container();
        container.setFacility(facility);
        container.setContainerCode(containerModelDTO.getContainerCode());
        container.setSize(typeContainer.getSize());
        container.setTypeContainer(typeContainer);
        container.setEmpty(containerModelDTO.getIsEmpty());
        container.setStatus(Constants.ContainerStatus.AVAILABLE.getStatus());
        container.setOwner(username);
        container.setUid(RandomUtils.getRandomId());
        container.setCreatedAt(System.currentTimeMillis());
        container.setUpdatedAt(System.currentTimeMillis());
        containerRepo.save(container);
        return convertToModel(container);
    }

    @Override
    public ContainerModel getContainerByUid(String uid) {
        Container container = containerRepo.findByUid(uid);
        ContainerModel containerModel = convertToModel(container);
        return containerModel;
    }

    @Override
    public ContainerModel updateContainer(ContainerModel containerModel) {
        Container container = containerRepo.findByUid(containerModel.getUid());
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
        if (containerModel.getOwner() != null) {
            container.setOwner(containerModel.getOwner());
        }
        container.setUpdatedAt(System.currentTimeMillis());
        containerRepo.save(container);
        ContainerModel containerModelUpdate = convertToModel(container);
        return containerModelUpdate;
    }

    @Override
    public ContainerModel deleteContainer(String uid) {
        Container container = containerRepo.findByUid(uid);
        TypeContainer typeContainer = container.getTypeContainer();
        typeContainer.setTotal(typeContainer.getTotal() - 1);
        container.setStatus(Constants.ContainerStatus.DELETE.getStatus());
        container = containerRepo.save(container);
        return convertToModel(container);
    }

    @Override
    public ContainerFilterRes filterContainer(ContainerFilterRequestDTO containerFilterRequestDTO) {
        ContainerFilterRes containerFilterRes = new ContainerFilterRes();

        String sql = "SELECT * FROM container_transport_container WHERE 1=1";
        String sqlCount = "SELECT COUNT(id) FROM container_transport_container WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();

        if(!StringUtils.isEmpty(containerFilterRequestDTO.getOwner())) {
            sql += " AND owner = :owner";
            sqlCount += " AND owner = :owner";
            params.put("owner", containerFilterRequestDTO.getOwner());
        }
        if(containerFilterRequestDTO.getContainerCode() != null) {
            sql += " AND container_code = :containerCode";
            sqlCount += " AND container_code = :containerCode";
            params.put("containerCode", containerFilterRequestDTO.getContainerCode());
        }
        if (containerFilterRequestDTO.getContainerSize() != null) {
            sql += " AND size = :size";
            sqlCount += " AND size = :size";
            params.put("size", containerFilterRequestDTO.getContainerSize());
        }

        if (containerFilterRequestDTO.getFacilityId() != null) {
            sql += " AND facility_id = :facilityId";
            sqlCount += " AND facility_id = :facilityId";
            params.put("facilityId", containerFilterRequestDTO.getFacilityId());
        }
        sql += " AND status != :statusNotEqual";
        sqlCount += " AND status != :statusNotEqual";
        params.put("statusNotEqual", Constants.ContainerStatus.DELETE.getStatus());

        Query queryCount = this.entityManager.createNativeQuery(sqlCount);
        for (String i : params.keySet()) {
            queryCount.setParameter(i, params.get(i));
        }
        containerFilterRes.setCount((Long) queryCount.getSingleResult());

        sql += " ORDER BY updated_at DESC";

        if (containerFilterRequestDTO.getPage() != null && containerFilterRequestDTO.getPageSize() != null) {
            sql += " LIMIT :pageSize OFFSET :index";
            params.put("pageSize", containerFilterRequestDTO.getPageSize());
            params.put("index", containerFilterRequestDTO.getPage() * containerFilterRequestDTO.getPageSize());
            containerFilterRes.setPage(containerFilterRequestDTO.getPage());
            containerFilterRes.setPageSize(containerFilterRequestDTO.getPageSize());
        }

        Query query = this.entityManager.createNativeQuery(sql, Container.class);
        for (String i : params.keySet()) {
            query.setParameter(i, params.get(i));
        }
        List<Container> truckModels = query.getResultList();
        List<ContainerModel> containerModelList = new ArrayList<>();
        truckModels.forEach((item) -> {
            containerModelList.add(convertToModel(item));
        });
        containerFilterRes.setContainerModels(containerModelList);
        return containerFilterRes;
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
        containerModel.setSize(container.getTypeContainer().getSize());
        containerModel.setIsEmpty(container.isEmpty());
        return containerModel;
    }
}
