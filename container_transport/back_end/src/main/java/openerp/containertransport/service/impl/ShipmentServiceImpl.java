package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.ShipmentModel;
import openerp.containertransport.dto.TripModel;
import openerp.containertransport.entity.Shipment;
import openerp.containertransport.repo.ShipmentRepo;
import openerp.containertransport.service.ShipmentService;
import openerp.containertransport.service.TripService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentRepo shipmentRepo;
    private final TripService tripService;
    private final ModelMapper modelMapper;
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

    public ShipmentModel convertToModel (Shipment shipment) {
        ShipmentModel shipmentModel = modelMapper.map(shipment, ShipmentModel.class);
        return shipmentModel;
    }
}
