package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.Collector;
import openerp.openerpresourceserver.entity.Driver;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.entity.Shipper;
import openerp.openerpresourceserver.entity.enumentity.Role;
import openerp.openerpresourceserver.repository.CollectorRepo;
import openerp.openerpresourceserver.repository.DriverRepo;
import openerp.openerpresourceserver.repository.HubRepo;
import openerp.openerpresourceserver.repository.ShipperRepo;
import openerp.openerpresourceserver.dto.EmployeeRequestDto;
import openerp.openerpresourceserver.dto.EmployeeDetailDTO;
import openerp.openerpresourceserver.dto.EmployeeResponse;
import openerp.openerpresourceserver.service.HumanResourceService;
import openerp.openerpresourceserver.service.KeycloakAdminClient;
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
    @Autowired
    private KeycloakAdminClient keycloakAdminClient;

    @Override
    public List<Shipper> getAllShipper(){
        return shipperRepo.findAll();
    }

    @Override
    public List<Shipper> getAllShippersByHubId(UUID hubId){
        return shipperRepo.getAllByHubId(hubId);
    }

    @Override
    public EmployeeDetailDTO getShipperById(UUID id) {
        Shipper shipper = shipperRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Shipper not found with id: " + id));

        // Chuyển đổi từ Entity sang eDTO
        return convertToResponseSDTO(shipper);
    }

    @Override
    public List<Collector> getAllCollector(){
        return collectorRepo.findAll();
    }

    @Override
    public List<Collector> getAllCollectorsByHubId(UUID hubId){
        return collectorRepo.getAllByHubId(hubId);
    }

    @Override
    public EmployeeDetailDTO getCollectorById(UUID id) {
        Collector collector = collectorRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Collector not found with id: " + id));

        // Chuyển đổi từ Entity sang eDTO
        return convertToResponseDTO(collector);
    }


//    @Override
//    public EmployeeDetailDTO getCollectorById(UUID id) {
//        Collector collector = collectorRepo.findById(id)
//                .orElseThrow(() -> new NotFoundException("Collector not found with id: " + id));
//
//        // Chuyển đổi từ Entity sang eDTO
//        return convertToResponseDTO(collector);
//    }

    @Override
    public List<Driver> getAllDriver(){
        return driverRepo.findAll();
    }

    @Override
    public EmployeeDetailDTO getDriverById(UUID id) {
        Driver driver = driverRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Driver not found with id: " + id));

        // Chuyển đổi từ Entity sang eDTO
        return convertToResponseDDTO(driver);
    }

    @Override
    public List<Driver> getAllDriversByHubId(UUID hubId){
        return driverRepo.getAllByHubId(hubId);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public EmployeeResponse addEmployee(EmployeeRequestDto employeeRequestDto) {

        try {

            // Register user in Keycloak
        keycloakAdminClient.createUserInKeycloak(employeeRequestDto);

        // Save employee information to the database based on their role
        Hub hub = hubRepo.findById(employeeRequestDto.getHub()).orElseThrow(() -> new NotFoundException("not found hub"));
        System.out.println(employeeRequestDto);
        if (employeeRequestDto.getRole() == Role.COLLECTOR) {
            Collector newCollector = new Collector();
            newCollector.setName(employeeRequestDto.getName());
            newCollector.setEmail(employeeRequestDto.getEmail());
            newCollector.setPhone(employeeRequestDto.getPhone());
            newCollector.setHubId(hub.getHubId());
            newCollector.setAddress(employeeRequestDto.getAddress());
            newCollector.setCity(employeeRequestDto.getCity());
            newCollector.setDistrict(employeeRequestDto.getDistrict());
            newCollector.setWard(employeeRequestDto.getWard());
            newCollector.setUsername(employeeRequestDto.getUsername());
            newCollector.setPassword(employeeRequestDto.getPassword());
            Collector saved = collectorRepo.save(newCollector);
            return new EmployeeResponse(saved.getId(), saved.getName());
        } else if (employeeRequestDto.getRole() == Role.DRIVER) {
            Driver newDriver = new Driver();
            newDriver.setName(employeeRequestDto.getName());
            newDriver.setEmail(employeeRequestDto.getEmail());
            newDriver.setPhone(employeeRequestDto.getPhone());
            newDriver.setOriginHubId(hub.getHubId());
            newDriver.setAddress(employeeRequestDto.getAddress());
            newDriver.setCity(employeeRequestDto.getCity());
            newDriver.setDistrict(employeeRequestDto.getDistrict());
            newDriver.setWard(employeeRequestDto.getWard());
            newDriver.setUsername(employeeRequestDto.getUsername());
            newDriver.setPassword(employeeRequestDto.getPassword());
            Driver saved = driverRepo.save(newDriver);
            return new EmployeeResponse(saved.getId(), saved.getName());
        } else if (employeeRequestDto.getRole() == Role.SHIPPER) {
            Shipper newShipper = new Shipper();
            newShipper.setName(employeeRequestDto.getName());
            newShipper.setEmail(employeeRequestDto.getEmail());
            newShipper.setPhone(employeeRequestDto.getPhone());
            newShipper.setHubId(hub.getHubId());
            newShipper.setAddress(employeeRequestDto.getAddress());
            newShipper.setCity(employeeRequestDto.getCity());
            newShipper.setDistrict(employeeRequestDto.getDistrict());
            newShipper.setWard(employeeRequestDto.getWard());
            newShipper.setUsername(employeeRequestDto.getUsername());
            newShipper.setPassword(employeeRequestDto.getPassword());
            Shipper saved = shipperRepo.save(newShipper);
            return new EmployeeResponse(saved.getId(), saved.getName());
        }

        throw new IllegalArgumentException("Invalid role: " + employeeRequestDto.getRole());
        }
        catch (RuntimeException e) {
            throw new IllegalStateException("Failed to create user in Keycloak", e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public EmployeeResponse updateEmployee(EmployeeRequestDto employeeRequestDto) {

        try {

            // Register user in Keycloak
            //keycloakAdminClient.updateUserInKeycloak(employeeRequest);

            // Save employee information to the database based on their role
            Hub hub = hubRepo.findById(employeeRequestDto.getHub()).orElseThrow(() -> new NotFoundException("not found hub"));
            System.out.println(employeeRequestDto);
            if (employeeRequestDto.getRole() == Role.COLLECTOR) {
                Collector newCollector = new Collector();
                newCollector.setId(employeeRequestDto.getId());
                newCollector.setName(employeeRequestDto.getName());
                newCollector.setEmail(employeeRequestDto.getEmail());
                newCollector.setPhone(employeeRequestDto.getPhone());
                newCollector.setHubId(hub.getHubId());
                newCollector.setAddress(employeeRequestDto.getAddress());
                newCollector.setCity(employeeRequestDto.getCity());
                newCollector.setDistrict(employeeRequestDto.getDistrict());
                newCollector.setWard(employeeRequestDto.getWard());
                newCollector.setUsername(employeeRequestDto.getUsername());
                newCollector.setPassword(employeeRequestDto.getPassword());
                Collector saved = collectorRepo.save(newCollector);
                return new EmployeeResponse(saved.getId(), saved.getName());
            } else if (employeeRequestDto.getRole() == Role.DRIVER) {
                Driver newDriver = new Driver();
                newDriver.setId(employeeRequestDto.getId());
                newDriver.setName(employeeRequestDto.getName());
                newDriver.setEmail(employeeRequestDto.getEmail());
                newDriver.setPhone(employeeRequestDto.getPhone());
                newDriver.setOriginHubId(hub.getHubId());
                newDriver.setAddress(employeeRequestDto.getAddress());
                newDriver.setCity(employeeRequestDto.getCity());
                newDriver.setDistrict(employeeRequestDto.getDistrict());
                newDriver.setWard(employeeRequestDto.getWard());
                newDriver.setUsername(employeeRequestDto.getUsername());
                newDriver.setPassword(employeeRequestDto.getPassword());
                Driver saved = driverRepo.save(newDriver);
                return new EmployeeResponse(saved.getId(), saved.getName());
            } else if (employeeRequestDto.getRole() == Role.SHIPPER) {
                Shipper newShipper = new Shipper();
                newShipper.setId(employeeRequestDto.getId());
                newShipper.setName(employeeRequestDto.getName());
                newShipper.setEmail(employeeRequestDto.getEmail());
                newShipper.setPhone(employeeRequestDto.getPhone());
                newShipper.setHubId(hub.getHubId());
                newShipper.setAddress(employeeRequestDto.getAddress());
                newShipper.setCity(employeeRequestDto.getCity());
                newShipper.setDistrict(employeeRequestDto.getDistrict());
                newShipper.setWard(employeeRequestDto.getWard());
                newShipper.setUsername(employeeRequestDto.getUsername());
                newShipper.setPassword(employeeRequestDto.getPassword());
                Shipper saved = shipperRepo.save(newShipper);
                return new EmployeeResponse(saved.getId(), saved.getName());
            }

            throw new IllegalArgumentException("Invalid role: " + employeeRequestDto.getRole());
        }
        catch (RuntimeException e) {
            throw new IllegalStateException("Failed to create user in Keycloak", e);
        }
    }
    @Override
    public Shipper addShipper(UUID shipperId, UUID hubId){
        Shipper shipper = shipperRepo.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper not found with id: " + shipperId));
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new RuntimeException("Hub not found with id: " + hubId));
        shipper.setHubId(hub.getHubId());
        return shipperRepo.save(shipper);
    }

    @Override
    public void removeShipper(UUID shipperId, UUID hubId){
        Shipper shipper = shipperRepo.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper not found with id: " + shipperId));
        shipper.setHubId(null);
        shipperRepo.save(shipper);
    }

    @Override
    public Collector addCollector(UUID collectorId, UUID hubId){
        Collector collector = collectorRepo.findById(collectorId)
                .orElseThrow(() -> new RuntimeException("Shipper not found with id: " + collectorId));
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new RuntimeException("Hub not found with id: " + hubId));
        collector.setHubId(hub.getHubId());
        return collectorRepo.save(collector);
    }

    @Override
    public void removeCollector(UUID collectorId, UUID hubId){
        Collector collector = collectorRepo.findById(collectorId)
                .orElseThrow(() -> new RuntimeException("Collector not found with id: " + collectorId));
        collector.setHubId(null);
        collectorRepo.save(collector);
    }


    @Override
    public Driver addDriverToOriginHub(UUID driverId, UUID hubId){
        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new RuntimeException("Hub not found with id: " + hubId));
        driver.setOriginHubId(hubId);
        return driverRepo.save(driver);
    }

    @Override
    public Driver addDriverToFinalHub(UUID driverId, UUID hubId){
        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new RuntimeException("Hub not found with id: " + hubId));
        driver.setFinalHubId(hubId);
        return driverRepo.save(driver);
    }

    @Override
    public void removeDriverFromOriginHub(UUID driverId, UUID hubId){
        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        driver.setOriginHubId(null);
        driverRepo.save(driver);
    }

    @Override
    public void removeDriverFromFinalHub(UUID driverId, UUID hubId){
        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        driver.setFinalHubId(null);
        driverRepo.save(driver);
    }

    @Override
    public void deleteShipper(UUID shipperId){
        Shipper shipper = shipperRepo.findById(shipperId).orElseThrow(() -> new NotFoundException("Shipper not found with id: " + shipperId));
        shipperRepo.delete(shipper);
    }

    @Override
    public void deleteCollector(UUID collectorId){
        System.out.println("delete");
        Collector collector = collectorRepo.findById(collectorId).orElseThrow(() -> new NotFoundException("Shipper not found with id: " + collectorId));
        collectorRepo.delete(collector);
    }

    @Override
    public void deleteDriver(UUID driverId){
        Driver driver = driverRepo.findById(driverId).orElseThrow(() -> new NotFoundException("Shipper not found with id: " + driverId));
        driverRepo.delete(driver);
    }

    private EmployeeDetailDTO convertToResponseDTO(Collector collector) {

        Hub hub = hubRepo.findById(collector.getHubId()).orElseThrow(() -> new NotFoundException("Hub not found"));

        return new EmployeeDetailDTO(
                collector.getId(),
                collector.getName(),
                collector.getUsername(),
                collector.getPassword(),
                collector.getEmail(),
                collector.getPhone(),
                collector.getAddress(),
                collector.getCity(),
                collector.getDistrict(),
                collector.getWard(),
                hub.getHubId(),
                hub.getName()
        );
    }
    private EmployeeDetailDTO convertToResponseSDTO(Shipper shipper) {
        Hub hub = hubRepo.findById(shipper.getHubId()).orElseThrow(() -> new NotFoundException("Hub not found"));

        return new EmployeeDetailDTO(
                shipper.getId(),
                shipper.getName(),
                shipper.getUsername(),
                shipper.getPassword(),
                shipper.getEmail(),
                shipper.getPhone(),
                shipper.getAddress(),
                shipper.getCity(),
                shipper.getDistrict(),
                shipper.getWard(),
                hub.getHubId(),
                hub.getName()
        );
    }

    private EmployeeDetailDTO convertToResponseDDTO(Driver collector) {

        return new EmployeeDetailDTO(
                collector.getId(),
                collector.getName(),
                collector.getUsername(),
                collector.getPassword(),
                collector.getEmail(),
                collector.getPhone(),
                collector.getAddress(),
                collector.getCity(),
                collector.getDistrict(),
                collector.getWard(),
                collector.getOriginHubId(),
                collector.getOriginHubName()
        );
    }


}
