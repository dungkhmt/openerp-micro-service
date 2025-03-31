package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.entity.Trip;
import openerp.openerpresourceserver.entity.Vehicle;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.mapper.VehicleMapper;
import openerp.openerpresourceserver.repository.RouteScheduleRepository;
import openerp.openerpresourceserver.repository.ScheduleVehicleAssignmentRepository;
import openerp.openerpresourceserver.repository.TripRepository;
import openerp.openerpresourceserver.repository.VehicleRepository;
import openerp.openerpresourceserver.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper vehicleMapper = VehicleMapper.INSTANCE;

    @Autowired
    private VehicleRepository vehicleRepo;
    @Autowired
    private TripRepository tripRepo;
    @Autowired
    private RouteScheduleRepository routeScheduleRepository;
    @Autowired
    private ScheduleVehicleAssignmentRepository scheduleVehicleAssignmentRepository;

    @Override
    public List<VehicleDto> getAllVehicleByHubId(UUID hubId){
        List<VehicleDto> vehicleDtos = vehicleRepo.getVehicleByHubId(hubId);
        if(vehicleDtos.isEmpty()) throw new NotFoundException("Not found any vehicle for hub");
        return vehicleDtos;
    }

    @Override
    public List<VehicleDto> getAll(){
        List<Vehicle> vehicles = vehicleRepo.findAll();
        if(vehicles.isEmpty()) throw new NotFoundException("Not found any vehicle");
        List<VehicleDto> vehicleDtos = new ArrayList<>();
        for(Vehicle vehicle : vehicles){
            vehicleDtos.add(vehicleMapper.vehicleToVehicleDto(vehicle));
        }
        return vehicleDtos;
    }

    @Override
    public VehicleDto getVehicleById(UUID vehicleId) {
        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + vehicleId));
        return vehicleMapper.vehicleToVehicleDto(vehicle);
    }

    @Override
    @Transactional
    public VehicleDto createOrUpdateVehicle(VehicleDto vehicleDto) {
        log.info("Creating new vehicle: {}", vehicleDto);

        Vehicle vehicle = vehicleMapper.vehicleDtoToVehicle(vehicleDto);

        // Set default status if not provided
        if (vehicle.getStatus() == null) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        }

        // Set createdAt date
        vehicle.setCreatedAt(new Date());

        Vehicle savedVehicle = vehicleRepo.save(vehicle);
        log.info("Created vehicle with ID: {}", savedVehicle.getVehicleId());

        return vehicleMapper.vehicleToVehicleDto(savedVehicle);
    }


    @Override
    @Transactional
    public void deleteVehicle(UUID vehicleId) {
        log.info("Deleting vehicle with ID: {}", vehicleId);

        if (!vehicleRepo.existsById(vehicleId)) {
            throw new NotFoundException("Vehicle not found with id: " + vehicleId);
        }

        // Check if vehicle is in use (assigned to route or has driver)
        Vehicle vehicle = vehicleRepo.findById(vehicleId).get();
        if (vehicle.getStatus() == VehicleStatus.ASSIGNED ||
                vehicle.getStatus() == VehicleStatus.TRANSITING) {
            throw new IllegalStateException("Cannot delete vehicle that is currently in use");
        }

        vehicleRepo.deleteById(vehicleId);
        log.info("Deleted vehicle with ID: {}", vehicleId);
    }
    @Override
    public VehicleDto getVehicleByTripId(UUID tripId) {
        Trip trip = tripRepo.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found with id: " + tripId));
        Vehicle vehicle = vehicleRepo.findById(trip.getVehicleId()).orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + tripId));
        return vehicleMapper.vehicleToVehicleDto(vehicle);
    }
    @Override
    public List<VehicleDto> getVehicleByTripIds(List<UUID> tripIds) {
        List<VehicleDto> vehicleDtos = new ArrayList<>();
        for(UUID tripId : tripIds){
        Trip trip = tripRepo.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found with id: " + tripId));
        Vehicle vehicle = vehicleRepo.findById(trip.getVehicleId()).orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + tripId));
            vehicleDtos.add(VehicleMapper.INSTANCE.vehicleToVehicleDto(vehicle));
        }
        return vehicleDtos;
    }
}
