package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.containertransport.dto.TruckModel;
import openerp.containertransport.entity.Truck;
import openerp.containertransport.repo.TruckRepo;
import openerp.containertransport.service.TruckService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class TruckServiceImpl implements TruckService  {
    private final TruckRepo truckRepo;

    @Override
    public Truck createTruck(TruckModel truckModel) {
        Truck truck = new Truck();
        truck.setFacilityId(truckModel.getFacilityId());
        truck.setDriverId(truckModel.getDriverId());
        truck.setLicensePlates(truckModel.getLicensePlates());
        truck.setBrandTruck(truckModel.getBrandTruck());
        truck.setCreatedAt(System.currentTimeMillis());
        truck.setUpdatedAt(System.currentTimeMillis());
        truck = truckRepo.save(truck);
        truck.setTruckCode("TR"+truck.getId());
        truck = truckRepo.save(truck);
        return truck;
    }
}
