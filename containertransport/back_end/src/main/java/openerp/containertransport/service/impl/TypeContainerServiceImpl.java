package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.containertransport.dto.TypeContainerFilterReqDTO;
import openerp.containertransport.dto.TypeContainerFilterRes;
import openerp.containertransport.dto.TypeContainerModel;
import openerp.containertransport.entity.Truck;
import openerp.containertransport.entity.TypeContainer;
import openerp.containertransport.repo.TypeContainerRepo;
import openerp.containertransport.service.TypeContainerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class TypeContainerServiceImpl implements TypeContainerService {
    private final TypeContainerRepo typeContainerRepo;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;
    @Override
    public TypeContainerModel createTypeContainer(TypeContainerModel typeContainerModel) {
        TypeContainer typeContainer = new TypeContainer();
        typeContainer.setSize(typeContainerModel.getSize());
        typeContainer.setTotal(0);
        typeContainer.setCreatedAt(System.currentTimeMillis());
        typeContainer.setUpdatedAt(System.currentTimeMillis());
        typeContainer = typeContainerRepo.save(typeContainer);
        typeContainer.setTypeContainerCode("TYCO" + typeContainer.getId());
        typeContainer = typeContainerRepo.save(typeContainer);
        return convertToModel(typeContainer);
    }

    @Override
    public TypeContainerFilterRes filterTypeContainer(TypeContainerFilterReqDTO typeContainerFilterReqDTO) {
        TypeContainerFilterRes typeContainerFilterRes = new TypeContainerFilterRes();

        String sql = "SELECT * FROM container_transport_type_container WHERE 1=1";
        String sqlCount = "SELECT COUNT(id) FROM container_transport_type_container WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();

        Query queryCount = this.entityManager.createNativeQuery(sqlCount);
        for (String i : params.keySet()) {
            queryCount.setParameter(i, params.get(i));
        }
        typeContainerFilterRes.setCount((Long) queryCount.getSingleResult());

        sql += " ORDER BY updated_at DESC";

        if (typeContainerFilterReqDTO.getPage() != null && typeContainerFilterReqDTO.getPageSize() != null) {
            sql += " LIMIT :pageSize OFFSET :index";
            params.put("pageSize", typeContainerFilterReqDTO.getPageSize());
            params.put("index", typeContainerFilterReqDTO.getPage() * typeContainerFilterReqDTO.getPageSize());
            typeContainerFilterRes.setPage(typeContainerFilterReqDTO.getPage());
            typeContainerFilterRes.setPageSize(typeContainerFilterReqDTO.getPageSize());
        }

        Query query = this.entityManager.createNativeQuery(sql, TypeContainer.class);
        for (String i : params.keySet()) {
            query.setParameter(i, params.get(i));
        }
        List<TypeContainer> typeContainers = query.getResultList();
        List<TypeContainerModel> typeContainerModels = new ArrayList<>();
        typeContainers.forEach((item) -> typeContainerModels.add(convertToModel(item)));
        typeContainerFilterRes.setTypeContainers(typeContainerModels);
        return typeContainerFilterRes;
    }

    public TypeContainerModel convertToModel (TypeContainer typeContainer) {
        TypeContainerModel typeContainerModel = modelMapper.map(typeContainer, TypeContainerModel.class);
        return typeContainerModel;
    }
}
