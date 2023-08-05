package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.containertransport.constants.Constants;
import openerp.containertransport.dto.*;
import openerp.containertransport.entity.Facility;
import openerp.containertransport.entity.Truck;
import openerp.containertransport.repo.FacilityRepo;
import openerp.containertransport.repo.TruckRepo;
import openerp.containertransport.service.TruckService;
import openerp.containertransport.utils.RandomUtils;
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
        facility.setNumberTruck(facility.getNumberTruck() == null ? 1 :  facility.getNumberTruck() + 1);
        facilityRepo.save(facility);
        Truck truck = new Truck();
        truck.setFacility(facility);
        truck.setDriverName(truckModel.getDriverName());
        truck.setDriverId(truckModel.getDriverId());
        truck.setLicensePlates(truckModel.getLicensePlates());
        truck.setBrandTruck(truckModel.getBrandTruck());
        truck.setStatus("AVAILABLE");
        truck.setUid(RandomUtils.getRandomId());
        truck.setYearOfManufacture(truckModel.getYearOfManufacture());
        truck.setCreatedAt(System.currentTimeMillis());
        truck.setUpdatedAt(System.currentTimeMillis());
        truck = truckRepo.save(truck);
        truck.setTruckCode("TR"+truck.getId());
        truck = truckRepo.save(truck);
        return truck;
    }

    @Override
    public TruckFilterRes filterTruck(TruckFilterRequestDTO truckFilterRequestDTO) {
        TruckFilterRes truckFilterRes = new TruckFilterRes();

        String sql = "SELECT * FROM container_transport_trucks WHERE 1=1";
        String sqlCount = "SELECT COUNT(id) FROM container_transport_trucks WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();

        if(truckFilterRequestDTO.getTruckCode() != null) {
            sql += " AND truck_code = :truckCode";
            sqlCount += " AND truck_code = :truckCode";
            params.put("truckCode", truckFilterRequestDTO.getTruckCode());
        }
        if(truckFilterRequestDTO.getFacilityId() != null) {
            sql += " AND facility_id = :facilityId";
            sqlCount += " AND facility_id = :facilityId";
            params.put("facilityId", truckFilterRequestDTO.getFacilityId());
        }
        if(truckFilterRequestDTO.getStatus() != null) {
            sql += " AND status = :status";
            sqlCount += " AND status = :status";
            params.put("status", truckFilterRequestDTO.getStatus());
        }
        sql += " AND status != :statusNotEqual";
        sqlCount += " AND status != :statusNotEqual";
        params.put("statusNotEqual", Constants.TruckStatus.DELETE.getStatus());

        Query queryCount = this.entityManager.createNativeQuery(sqlCount);
        for (String i : params.keySet()) {
            queryCount.setParameter(i, params.get(i));
        }
        truckFilterRes.setCount((Long) queryCount.getSingleResult());

        sql += " ORDER BY updated_at DESC";

        if (truckFilterRequestDTO.getPage() != null && truckFilterRequestDTO.getPageSize() != null) {
            sql += " LIMIT :pageSize OFFSET :index";
            params.put("pageSize", truckFilterRequestDTO.getPageSize());
            params.put("index", truckFilterRequestDTO.getPage() * truckFilterRequestDTO.getPageSize());
            truckFilterRes.setPage(truckFilterRequestDTO.getPage());
            truckFilterRes.setPageSize(truckFilterRequestDTO.getPageSize());
        }

        Query query = this.entityManager.createNativeQuery(sql, Truck.class);
        for (String i : params.keySet()) {
            query.setParameter(i, params.get(i));
        }
        List<Truck> trucks = query.getResultList();
        List<TruckModel> truckModels = new ArrayList<>();
        trucks.forEach((item) -> truckModels.add(convertToModel(item)));
        truckFilterRes.setTruckModels(truckModels);
        return truckFilterRes;
    }

    @Override
    public TruckModel getTruckByUid(String uid) {
        Truck truck = truckRepo.findByUid(uid);
        TruckModel truckModel = convertToModel(truck);
        return truckModel;
    }

    @Override
    public TruckModel updateTruck(TruckModel truckModel, String uid) {
        Truck truck = truckRepo.findByUid(uid);
        if (truckModel.getStatus() != null) {
            truck.setStatus(truckModel.getStatus());
        }
        if (truckModel.getFacilityId() != null && truckModel.getFacilityId() != truck.getFacility().getId()) {
            Facility facilityOld = facilityRepo.findById(truck.getFacility().getId()).get();
            facilityOld.setNumberTruck(facilityOld.getNumberTruck() - 1);
            facilityRepo.save(facilityOld);
            Facility facilityNew = facilityRepo.findById(truckModel.getFacilityId()).get();
            facilityNew.setNumberTruck(facilityNew.getNumberTruck() + 1);
            truck.setFacility(facilityNew);
        }
        if(truckModel.getDriverId() != null) {
            truck.setDriverId(truckModel.getDriverId());
            truck.setDriverName(truckModel.getDriverName());
        }
        if(truckModel.getBrandTruck() != null){
            truck.setBrandTruck(truckModel.getBrandTruck());
        }
        if(truckModel.getLicensePlates() != null){
            truck.setLicensePlates(truckModel.getLicensePlates());
        }
        if(truckModel.getYearOfManufacture() != null) {
            truck.setYearOfManufacture(truckModel.getYearOfManufacture());
        }
        truck.setUpdatedAt(System.currentTimeMillis());
        truckRepo.save(truck);
        TruckModel truckModelUpdate = convertToModel(truck);
        return truckModelUpdate;
    }

    @Override
    public TruckModel deleteTruck(String uid) {
        Truck truck = truckRepo.findByUid(uid);
        truck.setStatus(Constants.TruckStatus.DELETE.getStatus());
        truck = truckRepo.save(truck);
        Facility facility = facilityRepo.findByUid(truck.getFacility().getUid());
        facility.setNumberTruck(facility.getNumberTruck() - 1);
        facilityRepo.save(facility);
        return convertToModel(truck);
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
