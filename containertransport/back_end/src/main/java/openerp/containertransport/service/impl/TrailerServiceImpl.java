package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.FacilityResponsiveDTO;
import openerp.containertransport.dto.TrailerFilterRequestDTO;
import openerp.containertransport.dto.TrailerModel;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.entity.Trailer;
import openerp.containertransport.entity.Truck;
import openerp.containertransport.repo.FacilityRepo;
import openerp.containertransport.repo.TrailerRepo;
import openerp.containertransport.repo.TruckRepo;
import openerp.containertransport.service.TrailerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrailerServiceImpl implements TrailerService {
    private final TrailerRepo trailerRepo;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;
    private final FacilityRepo facilityRepo;
    private final TruckRepo truckRepo;
    @Override
    public TrailerModel createTrailer(TrailerModel trailerModel) {
        Trailer trailer = new Trailer();
        Facility facility = new Facility();
        if(trailerModel.getFacilityId() != null) {
            facility = facilityRepo.findById(trailerModel.getFacilityId());
        }
        trailer.setFacility(facility);
        trailer.setStatus("AVAILABLE");
        trailer.setCreatedAt(System.currentTimeMillis());
        trailer.setUpdatedAt(System.currentTimeMillis());
        trailerRepo.save(trailer);
        trailer.setTrailerCode("TRAILER" + trailer.getId());
        trailerRepo.save(trailer);
        return convertToModel(trailer);
    }

    @Override
    public TrailerModel getTrailerById(long id) {
        Trailer trailer = trailerRepo.findById(id);
        return convertToModel(trailer);
    }

    @Override
    public TrailerModel updateTrailer(TrailerModel trailerModel) {
        Trailer trailer = trailerRepo.findById(trailerModel.getId());
        if(trailerModel.getStatus() != null){
            trailer.setStatus(trailerModel.getStatus());
        }
        if(trailerModel.getFacilityId() != null) {
            Facility facility = facilityRepo.findById(trailerModel.getFacilityId());
            trailer.setFacility(facility);
        }
        if(trailerModel.getTruckId() != null) {
            Truck truck = truckRepo.findById(trailerModel.getTruckId());
            trailer.setTruck(truck);
        }
        trailer.setUpdatedAt(System.currentTimeMillis());
        trailerRepo.save(trailer);
        return convertToModel(trailer);
    }

    @Override
    public List<TrailerModel> filterTrailer(TrailerFilterRequestDTO trailerFilterRequestDTO) {
        String sql = "SELECT * FROM container_transport_trailers WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();
        if(trailerFilterRequestDTO.getStatus() != null){
            sql += " AND status = :status";
            params.put("status", trailerFilterRequestDTO.getStatus());
        }
        if(trailerFilterRequestDTO.getTruckId() != null) {
            sql += " AND truck_id = :truckId";
            params.put("truckId", trailerFilterRequestDTO.getTruckId());
        }
        if(trailerFilterRequestDTO.getFacilityId() != null) {
            sql += " AND facility_id = :facilityId";
            params.put("facilityId", trailerFilterRequestDTO.getFacilityId());
        }
        sql += " ORDER BY updated_at DESC";
        Query query = this.entityManager.createNativeQuery(sql, Trailer.class);
        for (String i : params.keySet()) {
            query.setParameter(i, params.get(i));
        }
        List<Trailer> trailers = query.getResultList();
        List<TrailerModel> trailerModels = new ArrayList<>();
        trailers.forEach((item) -> trailerModels.add(convertToModel(item)));
        return trailerModels;
    }

    public TrailerModel convertToModel(Trailer trailer){
        TrailerModel trailerModel = modelMapper.map(trailer, TrailerModel.class);
        FacilityResponsiveDTO facilityResponsiveDTO = new FacilityResponsiveDTO();
        facilityResponsiveDTO.setFacilityId(trailer.getFacility().getId());
        facilityResponsiveDTO.setFacilityCode(trailer.getFacility().getFacilityCode());
        facilityResponsiveDTO.setFacilityName(trailer.getFacility().getFacilityName());
        facilityResponsiveDTO.setLatitude(trailer.getFacility().getLatitude());
        facilityResponsiveDTO.setLongitude(trailer.getFacility().getLongitude());
        facilityResponsiveDTO.setAddress(trailer.getFacility().getAddress());
        trailerModel.setFacilityResponsiveDTO(facilityResponsiveDTO);
        return trailerModel;
    }
}
