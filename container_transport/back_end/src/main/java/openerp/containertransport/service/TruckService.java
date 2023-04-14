package openerp.containertransport.service;

import openerp.containertransport.dto.TruckModel;
import openerp.containertransport.entity.Truck;

public interface TruckService {
    Truck createTruck(TruckModel truckModel);
}
