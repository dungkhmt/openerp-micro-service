package openerp.containertransport.service;

import openerp.containertransport.dto.TruckFilterRequestDTO;
import openerp.containertransport.dto.TruckFilterRes;
import openerp.containertransport.dto.TruckModel;
import openerp.containertransport.entity.Truck;

import java.util.List;

public interface TruckService {
    Truck createTruck(TruckModel truckModel);
    TruckFilterRes filterTruck(TruckFilterRequestDTO truckFilterRequestDTO);
    TruckModel getTruckByUid(String uid);
    TruckModel updateTruck(TruckModel truckModel, String uid);
    TruckModel deleteTruck(String uid);
}
