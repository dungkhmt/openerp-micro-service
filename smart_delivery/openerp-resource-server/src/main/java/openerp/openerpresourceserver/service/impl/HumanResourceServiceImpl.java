package openerp.openerpresourceserver.service.impl;

import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.Collector;
import openerp.openerpresourceserver.entity.Driver;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.entity.Shipper;
import openerp.openerpresourceserver.repo.CollectorRepo;
import openerp.openerpresourceserver.repo.DriverRepo;
import openerp.openerpresourceserver.repo.HubRepo;
import openerp.openerpresourceserver.repo.ShipperRepo;
import openerp.openerpresourceserver.service.HumanResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class HumanResourceServiceImpl implements HumanResourceService {

    @Autowired
    private ShipperRepo shipperRepo;
    @Autowired
    private CollectorRepo collectorRepo;
    @Autowired
    private DriverRepo driverRepo;
    @Autowired
    private HubRepo hubRepo;

    @Override
    public List<Shipper> getAllShipper(){
        return shipperRepo.findAll();
    }

    @Override
    public Shipper GetShipperById(UUID id){
        return shipperRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipper not found with id: " + id));
    }

    @Override
    public List<Collector> getAllCollector(){
        return collectorRepo.findAll();
    }

    @Override
    public Collector getCollectorById(UUID id){
        return collectorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Collector not found with id: " + id));
    }

    @Override
    public List<Driver> getAllDriver(){
        return driverRepo.findAll();
    }

    @Override
    public Driver getDriverById(UUID id){
        return driverRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
    }

    public Shipper createShipper(Shipper shipper){
        if(shipper.getName() == null || shipper.getShipperId() == null){
            log.warn("Delivery person full name or user login id must not be null");
            return null;
        }
        shipperRepo.save(shipper);
        return shipper;
    }

    public Collector createCollector(Collector collector){
        if(collector.getName() == null || collector.getCollectorId() == null){
            log.warn("Delivery person full name or user login id must not be null");
            return null;
        }
        collectorRepo.save(collector);
        return collector;
    }

    public Driver createDriver(Driver driver){
        if(driver.getName() == null || driver.getDriverId() == null){
            log.warn("driver  full name or user login id must not be null");
            return null;
        }
        driverRepo.save(driver);
        return driver;
    }



    @Override
    public Shipper addShipper(UUID shipperId, UUID hubId){
        Shipper shipper = shipperRepo.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper not found with id: " + shipperId));
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new RuntimeException("Hub not found with id: " + hubId));
        shipper.setHub(hub);
        return shipperRepo.save(shipper);
    }

    @Override
    public void removeShipper(UUID shipperId, UUID hubId){
        Shipper shipper = shipperRepo.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper not found with id: " + shipperId));
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new RuntimeException("Hub not found with id: " + hubId));

        hub.getShipperList().remove(shipper);
        hubRepo.save(hub);
    }

    @Override
    public Collector addCollector(UUID collectorId, UUID hubId){
        Collector collector = collectorRepo.findById(collectorId)
                .orElseThrow(() -> new RuntimeException("Shipper not found with id: " + collectorId));
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new RuntimeException("Hub not found with id: " + hubId));
        collector.setHub(hub);
        return collectorRepo.save(collector);
    }

    @Override
    public void removeCollector(UUID collectorId, UUID hubId){
        Collector collector = collectorRepo.findById(collectorId)
                .orElseThrow(() -> new RuntimeException("Collector not found with id: " + collectorId));
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new RuntimeException("Hub not found with id: " + hubId));

        hub.getCollectorList().remove(collector);
        hubRepo.save(hub);
    }

    @Override
    public Driver addDriverToOriginHub(UUID driverId, UUID hubId){
        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new RuntimeException("Hub not found with id: " + hubId));
        driver.setOriginHub(hub);
        return driverRepo.save(driver);
    }

    @Override
    public Driver addDriverToFinalHub(UUID driverId, UUID hubId){
        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new RuntimeException("Hub not found with id: " + hubId));
        driver.setFinalHub(hub);
        return driverRepo.save(driver);
    }

    @Override
    public void removeDriverFromOriginHub(UUID driverId, UUID hubId){
        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new RuntimeException("Hub not found with id: " + hubId));
        hub.getOriginDriverList().remove(driver);
        hubRepo.save(hub);
    }

    @Override
    public void removeDriverFromFinalHub(UUID driverId, UUID hubId){
        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new RuntimeException("Hub not found with id: " + hubId));
        hub.getFinalDriverList().remove(driver);
        hubRepo.save(hub);
    }

}
