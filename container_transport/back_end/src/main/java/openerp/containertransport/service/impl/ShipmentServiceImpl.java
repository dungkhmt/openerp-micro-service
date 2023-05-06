package openerp.containertransport.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.ShipmentFilterRequestDTO;
import openerp.containertransport.dto.ShipmentModel;
import openerp.containertransport.dto.TripModel;
import openerp.containertransport.entity.Shipment;
import openerp.containertransport.repo.ShipmentRepo;
import openerp.containertransport.service.ShipmentService;
import openerp.containertransport.service.TripService;
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
        shipment.setStatus("Waiting");
        shipment.setCreatedAt(System.currentTimeMillis());
        shipment.setUpdatedAt(System.currentTimeMillis());
        shipment = shipmentRepo.save(shipment);
        shipment.setCode("SHMT" + shipment.getId());
        shipment = shipmentRepo.save(shipment);

        ShipmentModel shipmentModelCreate = convertToModel(shipment);

        List<TripModel> tripModels = new ArrayList<>();

        if (!shipmentModel.getTripList().isEmpty()) {
            Shipment finalShipment = shipment;
            shipmentModel.getTripList().forEach((item) -> {
                TripModel tripModel = tripService.createTrip(item, finalShipment.getId(), shipmentModel.getCreatedByUserId());
                tripModels.add(tripModel);
            });
        }
        shipmentModelCreate.setTripList(tripModels);

        return shipmentModelCreate;
    }

    @Override
    public List<ShipmentModel> filterShipment(ShipmentFilterRequestDTO requestDTO) {
        String sql = "SELECT * FROM container_transport_shipment WHERE 1=1";
        HashMap<String, Object> params = new HashMap<>();
        if (requestDTO.getStatus() != null) {
            sql += " AND status = :status";
            params.put("status", requestDTO.getStatus());
        }

        sql += " ORDER BY updated_at DESC";
        Query query = this.entityManager.createNativeQuery(sql, Shipment.class);
        for (String i : params.keySet()) {
            query.setParameter(i, params.get(i));
        }
        List<Shipment> shipments = query.getResultList();
        List<ShipmentModel> shipmentModels = new ArrayList<>();
        shipments.forEach((item) -> shipmentModels.add(convertToModel(item)));

        return shipmentModels;
    }

    public ShipmentModel convertToModel (Shipment shipment) {
        ShipmentModel shipmentModel = modelMapper.map(shipment, ShipmentModel.class);
        return shipmentModel;
    }
}
