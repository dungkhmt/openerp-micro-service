package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.Constants;
import openerp.containertransport.dto.ShipmentFilterRequestDTO;
import openerp.containertransport.dto.ShipmentModel;
import openerp.containertransport.dto.ShipmentRes;
import openerp.containertransport.dto.TripModel;
import openerp.containertransport.entity.Shipment;
import openerp.containertransport.repo.ShipmentRepo;
import openerp.containertransport.service.ShipmentService;
import openerp.containertransport.service.TripService;
import openerp.containertransport.utils.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentRepo shipmentRepo;
    private final TripService tripService;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;
    @Override
    public ShipmentModel createShipment(ShipmentModel shipmentModel) {
        Shipment shipment = new Shipment();

        shipment.setCreatedByUserId(shipmentModel.getCreatedByUserId());
        shipment.setStatus("WAITING_SCHEDULER");
        shipment.setUid(RandomUtils.getRandomId());
        shipment.setDescription(shipmentModel.getDescription());
        shipment.setExecuted_time(shipmentModel.getExecutedTime());
        shipment.setCreatedAt(System.currentTimeMillis());
        shipment.setUpdatedAt(System.currentTimeMillis());
        shipment = shipmentRepo.save(shipment);
        shipment.setCode("SHMT" + shipment.getId());
        shipment = shipmentRepo.save(shipment);

        ShipmentModel shipmentModelCreate = convertToModel(shipment);

//        List<TripModel> tripModels = new ArrayList<>();

//        if (!shipmentModel.getTripList().isEmpty()) {
//            Shipment finalShipment = shipment;
//            shipmentModel.getTripList().forEach((item) -> {
//                TripModel tripModel = tripService.createTrip(item, finalShipment.getId(), shipmentModel.getCreatedByUserId());
//                tripModels.add(tripModel);
//            });
//        }
//        shipmentModelCreate.setTripList(tripModels);

        return shipmentModelCreate;
    }

    @Override
    public ShipmentRes filterShipment(ShipmentFilterRequestDTO requestDTO) {
        ShipmentRes shipmentRes = new ShipmentRes();
        String sql = "SELECT * FROM container_transport_shipment WHERE 1=1";
        String sqlCount = "SELECT COUNT(id) FROM container_transport_shipment WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();
        if (requestDTO.getStatus() != null) {
            sql += " AND status = :status";
            sqlCount += " AND status = :status";
            params.put("status", requestDTO.getStatus());
        }
        Query queryCount = this.entityManager.createNativeQuery(sqlCount);
        for (String i : params.keySet()) {
            queryCount.setParameter(i, params.get(i));
        }
        shipmentRes.setCount((Long) queryCount.getSingleResult());

        sql += " ORDER BY updated_at DESC";

        if (requestDTO.getPage() != null && requestDTO.getPageSize() != null) {
            sql += " LIMIT :pageSize OFFSET :index";
            params.put("pageSize", requestDTO.getPageSize());
            params.put("index", requestDTO.getPage() * requestDTO.getPageSize());
            shipmentRes.setPage(requestDTO.getPage());
            shipmentRes.setPageSize(requestDTO.getPageSize());
        }
        Query query = this.entityManager.createNativeQuery(sql, Shipment.class);
        for (String i : params.keySet()) {
            query.setParameter(i, params.get(i));
        }
        List<Shipment> shipments = query.getResultList();
        List<ShipmentModel> shipmentModels = new ArrayList<>();
        shipments.forEach((item) -> shipmentModels.add(convertToModel(item)));
        shipmentRes.setShipmentModels(shipmentModels);
        return shipmentRes;
    }

    @Override
    public ShipmentModel getShipmentByUid(String uid) {
        Shipment shipment = shipmentRepo.findByUid(uid);
        return convertToModel(shipment);
    }

    @Override
    public ShipmentModel updateShipment(String uid, ShipmentModel shipmentModel) {
        Shipment shipment = shipmentRepo.findByUid(uid);
        if(!StringUtils.isEmpty(shipmentModel.getDescription())) {
            shipment.setDescription(shipmentModel.getDescription());
        }
        if(shipment.getExecuted_time() != null) {
            shipment.setExecuted_time(shipmentModel.getExecutedTime());
        }
        shipment = shipmentRepo.save(shipment);
        return convertToModel(shipment);
    }

    @Override
    public ShipmentModel deleteShipment(String uid) {
        Shipment shipment = shipmentRepo.findByUid(uid);
        if(shipment.getStatus().equals(Constants.ShipmentStatus.WAITING_SCHEDULED.getStatus())) {
            shipment.setStatus(Constants.ShipmentStatus.DELETE.getStatus());
            shipment = shipmentRepo.save(shipment);
        }
        return convertToModel(shipment);
    }

    public ShipmentModel convertToModel (Shipment shipment) {
        ShipmentModel shipmentModel = modelMapper.map(shipment, ShipmentModel.class);
        if(shipment.getExecuted_time() != null) {
            shipmentModel.setExecutedTime(shipment.getExecuted_time());
        }
        return shipmentModel;
    }
}
