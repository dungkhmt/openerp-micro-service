package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.DriverVehicleAssignmentDto;
import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.entity.Driver;
import openerp.openerpresourceserver.entity.Vehicle;
import openerp.openerpresourceserver.entity.VehicleDriver;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.mapper.VehicleMapper;
import openerp.openerpresourceserver.repository.DriverRepo;
import openerp.openerpresourceserver.repository.VehicleDriverRepository;
import openerp.openerpresourceserver.repository.VehicleRepository;
import openerp.openerpresourceserver.service.DriverVehicleAssignmentService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DriverVehicleAssignmentServiceImpl implements DriverVehicleAssignmentService {

    private final VehicleRepository vehicleRepo;
    private final VehicleDriverRepository vehicleDriverRepository;
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
        if (vehicleDriverRepository.existsByVehicleIdAndUnassignedAtIsNull(vehicleId)) {
            throw new IllegalStateException("Vehicle already has a driver assigned. Please unassign first.");
        }

        // Check if driver is already assigned to another vehicle
        boolean isDriverAssigned = vehicleDriverRepository.existsByDriverIdAndUnassignedAtIsNull(driverId);

        if (isDriverAssigned) {
            throw new IllegalStateException("Driver is already assigned to another vehicle. Please unassign first.");
        }

        // Assign driver to vehicle
        VehicleDriver vehicleDriver = new VehicleDriver();
        vehicleDriver.setVehicleId(vehicleId);
        vehicleDriver.setDriverId(driverId);

        vehicle.setUpdatedAt(new Date());

        vehicleRepo.save(vehicle);
        vehicleDriverRepository.save(vehicleDriver);
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

        VehicleDriver vehicleDriver = vehicleDriverRepository.findByVehicleIdAndUnassignedAtIsNull(vehicleId);
        if (vehicleDriver == null) throw new IllegalStateException("Vehicle does not have a driver assigned");
        vehicleDriver.setUnassignedAt(Timestamp.valueOf(LocalDateTime.now()));

        vehicleDriverRepository.save(vehicleDriver);
        log.info("Driver unassigned from vehicle {}", vehicleId);
    }

    @Override
    public VehicleDto getVehicleForDriver(UUID driverId) {
        log.info("Getting vehicle for driver {}", driverId);

        // Check if driver exists
        if (!driverRepo.existsById(driverId)) {
            throw new NotFoundException("Driver not found with id: " + driverId);
        }
        VehicleDriver vehicleDriver = vehicleDriverRepository.findByDriverIdAndUnassignedAtIsNull(driverId);
        if (vehicleDriver == null) throw new IllegalStateException("Driver does not have a vehicle assigned");
        Vehicle vehicle = vehicleRepo.findById(vehicleDriver.getVehicleId()).orElseThrow(()-> new NotFoundException("not found vehicle"));
        return vehicleMapper.vehicleToVehicleDto(vehicle);
    }

    @Override
    public List<VehicleDto> getUnassignedVehicles() {
        log.info("Getting all unassigned vehicles");
        List<VehicleDriver> assigned = vehicleDriverRepository.findByUnassignedAtIsNull();
        if(assigned.isEmpty())
            return vehicleRepo.findAll().stream()
                .map(vehicleMapper::vehicleToVehicleDto).toList();
        Set<UUID> assignedVehicles = assigned.stream().map(v-> v.getVehicleId()).collect(Collectors.toSet());
        List<Vehicle> unassignedVehicles = vehicleRepo.findAll().stream()
                .filter(v -> !assignedVehicles.contains(v.getVehicleId()))
                .toList();

        return unassignedVehicles.stream()
                .map(vehicleMapper::vehicleToVehicleDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverVehicleAssignmentDto> getAllDriverVehicleAssignments() {
        return vehicleDriverRepository.getAllDriverVehicleAssignments();
    }
}