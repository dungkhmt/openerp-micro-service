package openerp.containertransport.service.impl;

import com.graphhopper.ResponsePath;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.containertransport.dto.FacilityFilterRequestDTO;
import openerp.containertransport.dto.FacilityFilterRes;
import openerp.containertransport.dto.FacilityModel;
import openerp.containertransport.dto.map.MapReqDTO;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.entity.Relationship;
import openerp.containertransport.repo.FacilityRepo;
import openerp.containertransport.repo.RelationshipRepo;
import openerp.containertransport.service.FacilityService;
import openerp.containertransport.utils.GraphHopperCalculator;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

@Log4j2
@RequiredArgsConstructor
@Service
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepo facilityRepo;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;
    @Qualifier("threadPoolCreateCSVFileForQuote")
    private final ExecutorService threadPoolCreateCSVFileForQuote;
    private final GraphHopperCalculator graphHopperCalculator;
    private final RelationshipRepo relationshipRepo;

    @Override
    public Facility createFacility(FacilityModel facilityModel) throws Exception {
        Facility facility = new Facility();
        if(facilityModel.getAddress() != null) {
            MapReqDTO mapReqDTO = convertAddressToCoordinates(facilityModel.getAddress());
            facility.setLongitude(new BigDecimal(mapReqDTO.getLon()));
            facility.setLatitude(new BigDecimal(mapReqDTO.getLat()));
            facility.setAddress(facilityModel.getAddress());
        }
        facility.setFacilityName(facilityModel.getFacilityName());
        facility.setFacilityType(facilityModel.getFacilityType());
        facility.setMaxNumberContainer(facilityModel.getMaxNumberTrailer());
        facility.setMaxNumberTrailer(facilityModel.getMaxNumberContainer());
        facility.setMaxNumberTruck(facilityModel.getMaxNumberTruck());
        facility.setCreatedAt(System.currentTimeMillis());
        facility.setUpdatedAt(System.currentTimeMillis());
        facility.setStatus("available");
        facility.setProcessingTime(facilityModel.getProcessingTime());
//        ResponsePath responsePath = graphHopperCalculator.calculate(facility.getLatitude(), facility.getLongitude(),
//                new BigDecimal(21.032188), new BigDecimal(105.778867));
        facilityRepo.save(facility);
        facility.setFacilityCode("FACI" + facility.getId());
        facilityRepo.save(facility);
        threadPoolCreateCSVFileForQuote.submit(() -> {
            try {
                FacilityFilterRequestDTO requestDTO = new FacilityFilterRequestDTO();
                List<Facility> facilityModels = getAllFacility();
                facilityModels.forEach((item) -> {
                    if(item.getId() != facility.getId()) {
                        Relationship relationship1 = createRelationship(facility, item);
                        Relationship relationship2 = createRelationship(item, facility);
                        relationshipRepo.save(relationship1);
                        relationshipRepo.save(relationship2);
                    }
                });
            } catch (Exception e) {
                log.error("Calc distant facility error", e);
            }
        });
        return facility;
    }

    @Override
    public FacilityModel getFacilityById(long id) {
        Facility facility = facilityRepo.findById(id);
        FacilityModel facilityModel = convertToModel(facility);
        return facilityModel;
    }

    @Override
    public FacilityFilterRes filterFacility(FacilityFilterRequestDTO facilityFilterRequestDTO) {
        FacilityFilterRes facilityFilterRes = new FacilityFilterRes();

        String sql = "SELECT * FROM container_transport_facility WHERE 1=1";
        String sqlCount = "SELECT COUNT(id) FROM container_transport_facility WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();

        if(!StringUtils.isEmpty(facilityFilterRequestDTO.getFacilityName())) {
            sql += " AND facility_name = '%:facilityName%'";
            sqlCount += " AND facility_name = '%:facilityName%'";
            params.put("facilityName", facilityFilterRequestDTO.getFacilityName());
        }
        if(!StringUtils.isEmpty(facilityFilterRequestDTO.getType())) {
            sql += " AND facility_type = :type";
            sqlCount += " AND facility_type = :type";
            params.put("type", facilityFilterRequestDTO.getType());
        }
        Query queryCount = this.entityManager.createNativeQuery(sqlCount);
        for (String i : params.keySet()) {
            queryCount.setParameter(i, params.get(i));
        }
        facilityFilterRes.setCount((Long) queryCount.getSingleResult());

        sql += " ORDER BY updated_at DESC";

        if (facilityFilterRequestDTO.getPage() != null && facilityFilterRequestDTO.getPageSize() != null) {
            sql += " LIMIT :pageSize OFFSET :index";
            params.put("pageSize", facilityFilterRequestDTO.getPageSize());
            params.put("index", facilityFilterRequestDTO.getPage() * facilityFilterRequestDTO.getPageSize());
            facilityFilterRes.setPage(facilityFilterRequestDTO.getPage());
            facilityFilterRes.setPageSize(facilityFilterRequestDTO.getPageSize());
        }

        Query query = this.entityManager.createNativeQuery(sql, Facility.class);
        for (String i : params.keySet()) {
            query.setParameter(i, params.get(i));
        }
        List<Facility> facilities = query.getResultList();
        List<FacilityModel> facilityModels = new ArrayList<>();
        facilities.forEach((item) -> {
            facilityModels.add(convertToModel(item));
        });
        facilityFilterRes.setFacilityModels(facilityModels);
        return facilityFilterRes;
    }

    @Override
    public FacilityModel updateFacility(FacilityModel facilityModel) {
        Facility facility = facilityRepo.findById(facilityModel.getId());
        facility.setFacilityName(facilityModel.getFacilityName());
        facility.setUpdatedAt(System.currentTimeMillis());
        facilityRepo.save(facility);
        return convertToModel(facility);
    }

    FacilityModel convertToModel (Facility facility) {
        FacilityModel facilityModel = modelMapper.map(facility, FacilityModel.class);
        return facilityModel;
    }
    List<Facility> getAllFacility () {
        return facilityRepo.findAll();
    }

    public MapReqDTO convertAddressToCoordinates (String address) {
        RestTemplate restTemplate = new RestTemplate();
        String addressTmp = address.replace(",", "");
        String url = "https://nominatim.openstreetmap.org/search?q="+addressTmp+"&format=json&addressdetails=1&limit=1&polygon_svg=1";
        Object coordinates = restTemplate.getForObject(url, ArrayList.class).get(0);
        MapReqDTO mapReqDTO = modelMapper.map(coordinates, MapReqDTO.class);
        return mapReqDTO;
    }

    public Relationship createRelationship (Facility fromFacility, Facility toFacility) {
        Relationship relationship = new Relationship();
        relationship.setFromFacility(fromFacility.getId());
        relationship.setToFacility(toFacility.getId());
        ResponsePath responsePath = null;
        try {
            responsePath = graphHopperCalculator.calculate(fromFacility.getLatitude(), fromFacility.getLongitude(),
                    toFacility.getLatitude(), toFacility.getLongitude());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        relationship.setDistant(BigDecimal.valueOf(responsePath.getDistance()));
        relationship.setTime(responsePath.getTime());
        return relationship;
    }
}
