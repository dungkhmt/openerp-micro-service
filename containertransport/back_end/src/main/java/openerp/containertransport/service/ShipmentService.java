package openerp.containertransport.service;

import openerp.containertransport.dto.ShipmentFilterRequestDTO;
import openerp.containertransport.dto.ShipmentModel;
import openerp.containertransport.entity.Shipment;

import java.util.List;

public interface ShipmentService {
    ShipmentModel createShipment(ShipmentModel shipmentModel);
    List<ShipmentModel> filterShipment(ShipmentFilterRequestDTO requestDTO);
    ShipmentModel getShipmentByShipmentId(Long shipmentId);
}
