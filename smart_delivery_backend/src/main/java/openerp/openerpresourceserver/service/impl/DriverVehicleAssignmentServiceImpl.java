package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.DriverVehicleAssignmentDto;
import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.entity.Driver;
import openerp.openerpresourceserver.entity.Vehicle;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.mapper.VehicleMapper;
import openerp.openerpresourceserver.repository.DriverRepo;
import openerp.openerpresourceserver.repository.VehicleRepository;
import openerp.openerpresourceserver.service.DriverVehicleAssignmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DriverVehicleAssignmentServiceImpl implements DriverVehicleAssignmentService {

    private final VehicleRepository vehicleRepo;
    private final DriverRepo driverRepo;
    private final VehicleMapper vehicleMapper = VehicleMapper.INSTANCE;

    @Override
    @Transactional
    public void assignDriverToVehicle(UUID driverId, UUID vehicleId) {
        log.info("Assigning driver {} to vehicle {}", driverId, vehicleId);

        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new NotFoundException("Driver not found with id: " + driverId));

        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + vehicleId));

        // Check if vehicle already has a driver
        if (vehicle.getDriverId() != null) {
            throw new IllegalStateException("Vehicle already has a driver assigned. Please unassign first.");
        }

        // Check if driver is already assigned to another vehicle
        boolean isDriverAssigned = vehicleRepo.findAll().stream()
                .anyMatch(v -> driverId.equals(v.getDriverId()));

        if (isDriverAssigned) {
            throw new IllegalStateException("Driver is already assigned to another vehicle. Please unassign first.");
        }

        // Assign driver to vehicle
        vehicle.setDriverId(driver.getId());
        vehicle.setDriverName(driver.getName());
        vehicle.setDriverCode(driver.getCode());
        vehicle.setUpdatedAt(new Date());

        vehicleRepo.save(vehicle);
        log.info("Driver {} assigned to vehicle {}", driverId, vehicleId);
    }

    @Override
    @Transactional
    public void unassignDriverFromVehicle(UUID vehicleId) {
        log.info("Unassigning driver from vehicle {}", vehicleId);

        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + vehicleId));

        // Check if vehicle is in use
        if (vehicle.getStatus() == VehicleStatus.TRANSITING) {
            throw new IllegalStateException("Cannot unassign driver from vehicle that is currently in transit");
        }

        // Unassign driver
        vehicle.setDriverId(null);
        vehicle.setDriverName(null);
        vehicle.setDriverCode(null);
        vehicle.setUpdatedAt(new Date());

        vehicleRepo.save(vehicle);
        log.info("Driver unassigned from vehicle {}", vehicleId);
    }

    @Override
    public VehicleDto getVehicleForDriver(UUID driverId) {
        log.info("Getting vehicle for driver {}", driverId);

        // Check if driver exists
        if (!driverRepo.existsById(driverId)) {
            throw new NotFoundException("Driver not found with id: " + driverId);
        }

        Vehicle vehicle = vehicleRepo.findAll().stream()
                .filter(v -> driverId.equals(v.getDriverId()))
                .findFirst()
                .orElse(null);

        return vehicle != null ? vehicleMapper.vehicleToVehicleDto(vehicle) : null;
    }

    @Override
    public List<VehicleDto> getUnassignedVehicles() {
        log.info("Getting all unassigned vehicles");

        List<Vehicle> unassignedVehicles = vehicleRepo.findAll().stream()
                .filter(v -> v.getDriverId() == null)
                .collect(Collectors.toList());

        return unassignedVehicles.stream()
                .map(vehicleMapper::vehicleToVehicleDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverVehicleAssignmentDto> getAllDriverVehicleAssignments() {
        log.info("Getting all driver-vehicle assignments");

        List<DriverVehicleAssignmentDto> assignments = new ArrayList<>();

        List<Vehicle> vehicles = vehicleRepo.findAll().stream()
                .filter(v -> v.getDriverId() != null)
                .collect(Collectors.toList());

        for (Vehicle vehicle : vehicles) {
            Driver driver = driverRepo.findById(vehicle.getDriverId()).orElse(null);

            if (driver != null) {
                DriverVehicleAssignmentDto assignment = DriverVehicleAssignmentDto.builder()
                        .vehicleId(vehicle.getVehicleId())
                        .plateNumber(vehicle.getPlateNumber())
                        .vehicleType(vehicle.getVehicleType())
                        .model(vehicle.getModel())
                        .manufacturer(vehicle.getManufacturer())
                        .status(vehicle.getStatus())
                        .driverId(driver.getId())
                        .driverName(driver.getName())
                        .driverCode(driver.getCode())
                        .driverPhone(driver.getPhone())
                        .build();

                assignments.add(assignment);
            }
        }

        return assignments;
    }
}