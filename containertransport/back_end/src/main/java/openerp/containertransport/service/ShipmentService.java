package openerp.containertransport.service;

import openerp.containertransport.dto.ShipmentFilterRequestDTO;
import openerp.containertransport.dto.ShipmentModel;
import openerp.containertransport.dto.ShipmentRes;
import openerp.containertransport.entity.Shipment;

import java.util.List;

public interface ShipmentService {
    ShipmentModel createShipment(ShipmentModel shipmentModel);
    ShipmentRes filterShipment(ShipmentFilterRequestDTO requestDTO);
    ShipmentModel getShipmentByUid(String uid);
    ShipmentModel updateShipment(String uid, ShipmentModel shipmentModel);
    ShipmentModel deleteShipment(String uid);
}
