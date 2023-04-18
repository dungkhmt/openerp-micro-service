package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.containertransport.dto.FacilityFilterRequestDTO;
import openerp.containertransport.dto.FacilityModel;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.repo.FacilityRepo;
import openerp.containertransport.service.FacilityService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepo facilityRepo;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;

    @Override
    public Facility createFacility(FacilityModel facilityModel) {
        Facility facility = new Facility();
        facility.setFacilityName(facilityModel.getFacilityName());
        facility.setFacilityType(facilityModel.getFacilityType());
        facility.setMaxNumberContainer(facilityModel.getMaxNumberTrailer());
        facility.setMaxNumberTrailer(facilityModel.getMaxNumberContainer());
        facility.setMaxNumberTruck(facilityModel.getMaxNumberTruck());
        facility.setCreatedAt(System.currentTimeMillis());
        facility.setUpdatedAt(System.currentTimeMillis());
        facilityRepo.save(facility);
        facility.setFacilityCode("FACI" + facility.getId());
        facilityRepo.save(facility);
        return facility;
    }

    @Override
    public FacilityModel getFacilityById(long id) {
        Facility facility = facilityRepo.findById(id);
        FacilityModel facilityModel = convertToModel(facility);
        return facilityModel;
    }

    @Override
    public List<FacilityModel> filterFacility(FacilityFilterRequestDTO facilityFilterRequestDTO) {
        String sql = "SELECT * FROM container_transport_facility WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();
        if(facilityFilterRequestDTO.getFacilityName() != null) {
            sql += " AND facility_name = '%:facilityName%'";
            params.put("facilityName", facilityFilterRequestDTO.getFacilityName());
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
        return facilityModels;
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
}
