package openerp.openerpresourceserver.service;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.entity.Collector;
import openerp.openerpresourceserver.entity.Driver;
import openerp.openerpresourceserver.entity.Shipper;
import openerp.openerpresourceserver.dto.EmployeeRequestDto;
import openerp.openerpresourceserver.dto.EmployeeDetailDTO;
import openerp.openerpresourceserver.dto.EmployeeResponse;

import java.util.List;
import java.util.UUID;

public interface HumanResourceService {
    List<Shipper> getAllShipper();

    EmployeeDetailDTO getShipperById(UUID id);

    List<Collector> getAllCollector();

    List<Collector> getAllCollectorsByHubId(UUID hubId);

    EmployeeDetailDTO getCollectorById(UUID id);

    List<Driver> getAllDriver();

    EmployeeDetailDTO getDriverById(UUID id);


    EmployeeResponse addEmployee(EmployeeRequestDto employeeRequestDto);

    @Transactional(rollbackOn = Exception.class)
    EmployeeResponse updateEmployee(EmployeeRequestDto employeeRequestDto);

    Shipper addShipper(UUID shipperId, UUID hubId);

    void removeShipper(UUID shipperId, UUID hubId);

    Collector addCollector(UUID collectorId, UUID hubId);

    void removeCollector(UUID collectorId, UUID hubId);

    Driver addDriverToOriginHub(UUID driverId, UUID hubId);

    Driver addDriverToFinalHub(UUID driverId, UUID hubId);

    void removeDriverFromOriginHub(UUID driverId, UUID hubId);

    void removeDriverFromFinalHub(UUID driverId, UUID hubId);

    void deleteShipper(UUID shipperId);

    void deleteCollector(UUID collectorId);

    void deleteDriver(UUID driverId);

    List<Driver> getAllDriversByHubId(UUID hubId);

    List<Shipper> getAllShippersByHubId(UUID hubId);
}
