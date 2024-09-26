package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Collector;
import openerp.openerpresourceserver.entity.Driver;
import openerp.openerpresourceserver.entity.Shipper;

import java.util.List;
import java.util.UUID;

public interface HumanResourceService {
    List<Shipper> getAllShipper();

    Shipper GetShipperById(UUID id);

    List<Collector> getAllCollector();

    Collector getCollectorById(UUID id);

    List<Driver> getAllDriver();

    Driver getDriverById(UUID id);

    Shipper addShipper(UUID shipperId, UUID hubId);

    void removeShipper(UUID shipperId, UUID hubId);

    Collector addCollector(UUID collectorId, UUID hubId);

    void removeCollector(UUID collectorId, UUID hubId);

    Driver addDriverToOriginHub(UUID driverId, UUID hubId);

    Driver addDriverToFinalHub(UUID driverId, UUID hubId);

    void removeDriverFromOriginHub(UUID driverId, UUID hubId);

    void removeDriverFromFinalHub(UUID driverId, UUID hubId);
}
