package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.containertransport.dto.FacilityModel;
import openerp.containertransport.dto.FacilityResponsiveDTO;
import openerp.containertransport.dto.TruckFilterRequestDTO;
import openerp.containertransport.dto.TruckModel;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.entity.Truck;
import openerp.containertransport.repo.FacilityRepo;
import openerp.containertransport.repo.TruckRepo;
import openerp.containertransport.service.TruckService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class TruckServiceImpl implements TruckService  {
    private final TruckRepo truckRepo;
    private final FacilityRepo facilityRepo;
    private final EntityManager entityManager;

    @Override
    public Truck createTruck(TruckModel truckModel) {
        Facility facility = facilityRepo.findById(truckModel.getFacilityId()).get();
        Truck truck = new Truck();
        truck.setFacility(facility);
        truck.setDriverId(truckModel.getDriverId());
        truck.setLicensePlates(truckModel.getLicensePlates());
        truck.setBrandTruck(truckModel.getBrandTruck());
        truck.setStatus("AVAILABLE");
        truck.setYearOfManufacture(truckModel.getYearOfManufacture());
        truck.setCreatedAt(System.currentTimeMillis());
        truck.setUpdatedAt(System.currentTimeMillis());
        truck = truckRepo.save(truck);
        truck.setTruckCode("TR"+truck.getId());
        truck = truckRepo.save(truck);
        return truck;
    }

    @Override
    public List<TruckModel> filterTruck(TruckFilterRequestDTO truckFilterRequestDTO) {
        String sql = "SELECT * FROM container_transport_trucks WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();
        if(truckFilterRequestDTO.getTruckCode() != null) {
            sql += " AND truck_code = :truckCode";
            params.put("truckCode", truckFilterRequestDTO.getTruckCode());
        }
        if(truckFilterRequestDTO.getStatus() != null) {
            sql += " AND status = :status";
            params.put("status", truckFilterRequestDTO.getStatus());
        }
        sql += " ORDER BY updated_at DESC";

        Query query = this.entityManager.createNativeQuery(sql, Truck.class);
        for (String i : params.keySet()) {
            query.setParameter(i, params.get(i));
        }
        List<Truck> trucks = query.getResultList();
        List<TruckModel> truckModels = new ArrayList<>();
        trucks.forEach((item) -> truckModels.add(convertToModel(item)));
        return truckModels;
    }

    @Override
    public TruckModel getTruckById(long id) {
        Truck truck = truckRepo.findById(id);
        TruckModel truckModel = convertToModel(truck);
        return truckModel;
    }

    @Override
    public TruckModel updateTruck(TruckModel truckModel) {
        Truck truck = truckRepo.findById(truckModel.getId());
        if (truckModel.getStatus() != null) {
            truck.setStatus(truckModel.getStatus());
        }
        if (truckModel.getFacilityId() != null) {
            Facility facility = facilityRepo.findById(truckModel.getFacilityId()).get();
            truck.setFacility(facility);
        }
//        if(truckModel.getDriverId() != null){
//            truck.setFacilityId(truckModel.getFacilityId());
//        }
        truck.setUpdatedAt(System.currentTimeMillis());
        truckRepo.save(truck);
        TruckModel truckModelUpdate = convertToModel(truck);
        return truckModelUpdate;
    }

    public TruckModel convertToModel(Truck truck) {
        ModelMapper modelMapper = new ModelMapper();
        TruckModel truckModel = modelMapper.map(truck, TruckModel.class);
        FacilityResponsiveDTO facilityResponsiveDTO = new FacilityResponsiveDTO();
        facilityResponsiveDTO.setFacilityId(truck.getFacility().getId());
        facilityResponsiveDTO.setFacilityCode(truck.getFacility().getFacilityCode());
        facilityResponsiveDTO.setFacilityName(truck.getFacility().getFacilityName());
        facilityResponsiveDTO.setLatitude(truck.getFacility().getLatitude());
        facilityResponsiveDTO.setLongitude(truck.getFacility().getLongitude());
        facilityResponsiveDTO.setAddress(truck.getFacility().getAddress());
        truckModel.setFacilityResponsiveDTO(facilityResponsiveDTO);
        return truckModel;
    }
}
