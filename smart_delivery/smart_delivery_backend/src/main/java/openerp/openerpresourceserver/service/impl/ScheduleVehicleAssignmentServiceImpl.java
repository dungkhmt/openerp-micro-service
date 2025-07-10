package openerp.openerpresourceserver.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.RouteDto;
import openerp.openerpresourceserver.dto.RouteScheduleDto;
import openerp.openerpresourceserver.dto.RouteStopDto;
import openerp.openerpresourceserver.dto.ScheduleVehicleAssignmentDto;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.mapper.RouteMapper;
import openerp.openerpresourceserver.mapper.RouteScheduleMapper;
import openerp.openerpresourceserver.mapper.RouteStopMapper;
import openerp.openerpresourceserver.mapper.ScheduleVehicleAssignmentMapper;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.ScheduleVehicleAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleVehicleAssignmentServiceImpl implements ScheduleVehicleAssignmentService {

    private final ScheduleVehicleAssignmentRepository assignmentRepository;
    private final RouteScheduleRepository scheduleRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepo driverRepository;
    private final ScheduleVehicleAssignmentMapper mapper = ScheduleVehicleAssignmentMapper.INSTANCE;
    private final VehicleDriverRepository vehicleDriverRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;

    @Override
    @Transactional
    public ScheduleVehicleAssignmentDto assignVehicleToSchedule(
            UUID scheduleId, UUID vehicleId, LocalDate assignmentDate) {

        // Validate route schedule
        RouteSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Route schedule not found with ID: " + scheduleId));

        // Validate vehicle
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with ID: " + vehicleId));


        // Check if vehicle is already assigned to this schedule on this date
        assignmentRepository.findByRouteScheduleIdAndVehicleIdAndIsActiveTrue(scheduleId, vehicleId)
                .ifPresent(existing -> {
                    throw new IllegalStateException("Vehicle is already assigned to this schedule");
                });

        // Create a new assignment
        ScheduleVehicleAssignment assignment = ScheduleVehicleAssignment.builder()
                .routeScheduleId(scheduleId)
                .vehicleId(vehicleId)
                .vehiclePlateNumber(vehicle.getPlateNumber())
                .assignmentDate(assignmentDate)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // Update vehicle status
        vehicle.setStatus(VehicleStatus.ASSIGNED);
        vehicleRepository.save(vehicle);

        // Save and return the assignment
        ScheduleVehicleAssignment savedAssignment = assignmentRepository.save(assignment);
        return enrichAssignmentDto(mapper.assignmentToDto(savedAssignment));
    }

    @Override
    @Transactional
    public ScheduleVehicleAssignmentDto updateAssignment(UUID assignmentId, ScheduleVehicleAssignmentDto assignmentDto) {
        // Find existing assignment
        ScheduleVehicleAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with ID: " + assignmentId));

        // If vehicle is changing, validate the new vehicle
        if (!assignment.getVehicleId().equals(assignmentDto.getVehicleId())) {
            Vehicle newVehicle = vehicleRepository.findById(assignmentDto.getVehicleId())
                    .orElseThrow(() -> new NotFoundException("Vehicle not found with ID: " + assignmentDto.getVehicleId()));

            if (newVehicle.getStatus() != VehicleStatus.AVAILABLE && newVehicle.getStatus() != VehicleStatus.ASSIGNED) {
                throw new IllegalStateException("New vehicle is not available for assignment");
            }

            // Update old vehicle status if necessary
            Vehicle oldVehicle = vehicleRepository.findById(assignment.getVehicleId()).orElse(null);
            if (oldVehicle != null && oldVehicle.getStatus() == VehicleStatus.ASSIGNED) {
                oldVehicle.setStatus(VehicleStatus.AVAILABLE);
                vehicleRepository.save(oldVehicle);
            }

            // Update new vehicle status
            newVehicle.setStatus(VehicleStatus.ASSIGNED);
            vehicleRepository.save(newVehicle);

            // Update assignment with new vehicle
            assignment.setVehicleId(newVehicle.getVehicleId());
            assignment.setVehiclePlateNumber(newVehicle.getPlateNumber());
        }

        // If driver is changing, validate the new driver
        if ((assignment.getDriverId() == null && assignmentDto.getDriverId() != null) ||
                (assignment.getDriverId() != null && !assignment.getDriverId().equals(assignmentDto.getDriverId()))) {

            if (assignmentDto.getDriverId() != null) {
                var driver = driverRepository.findById(assignmentDto.getDriverId())
                        .orElseThrow(() -> new NotFoundException("Driver not found with ID: " + assignmentDto.getDriverId()));

                assignment.setDriverId(driver.getId());
                assignment.setDriverName(driver.getName());
            } else {
                assignment.setDriverId(null);
                assignment.setDriverName(null);
            }
        }

        // Update other fields
        assignment.setAssignmentDate(assignmentDto.getAssignmentDate());
        assignment.setActive(assignmentDto.isActive());
        assignment.setUpdatedAt(Instant.now());

        // Save and return
        ScheduleVehicleAssignment updatedAssignment = assignmentRepository.save(assignment);
        return enrichAssignmentDto(mapper.assignmentToDto(updatedAssignment));
    }

    @Override
    public List<ScheduleVehicleAssignmentDto> getAssignmentsByDriverUsername(String username){
        Driver driver = driverRepository.findByUsername(username);
        if(driver == null){throw new NotFoundException("Driver not found with username: " + username);}

        VehicleDriver vehicleDriver = vehicleDriverRepository.findByDriverIdAndUnassignedAtIsNull(driver.getId());

        List<ScheduleVehicleAssignment> assignments = assignmentRepository.findAllByVehicleId(vehicleDriver.getVehicleId());

        List<ScheduleVehicleAssignmentDto> assignmentDtos = assignments.stream().map(mapper::assignmentToDto).collect(Collectors.toList());

        for(ScheduleVehicleAssignmentDto assignmentDto : assignmentDtos){
            RouteSchedule routeSchedule = scheduleRepository.findById(assignmentDto.getRouteScheduleId()).orElseThrow(() -> new NotFoundException("Route schedule not found with ID: " + assignmentDto.getRouteScheduleId()));
            RouteScheduleDto routeScheduleDto = RouteScheduleMapper.INSTANCE.routeScheduleToDto(routeSchedule);

            Route route = routeRepository.findById(routeSchedule.getRouteId()).orElseThrow(()-> new NotFoundException("Route not found with ID: " + routeSchedule.getRouteId()));
            RouteDto routeDto = RouteMapper.INSTANCE.routeToRouteDto(route);

            List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequence(route.getRouteId());
            List<RouteStopDto> routeStopDtos = routeStops.stream().map(RouteStopMapper.INSTANCE::routeStopToRouteStopDto).toList();

            routeDto.setStops(routeStopDtos);
            assignmentDto.setRouteDto(routeDto);
            assignmentDto.setRouteScheduleDto(routeScheduleDto);
        }

        return assignmentDtos;
    }

    @Override
    @Transactional
    public void unassignVehicle(UUID assignmentId) {
        // Find assignment
        ScheduleVehicleAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with ID: " + assignmentId));

        // Update vehicle status
        Vehicle vehicle = vehicleRepository.findById(assignment.getVehicleId()).orElse(null);
        if (vehicle != null && vehicle.getStatus() == VehicleStatus.ASSIGNED) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);
        }

        // Mark as unassigned
        assignment.setActive(false);
        assignment.setUnassignedAt(Instant.now());
        assignment.setUpdatedAt(Instant.now());
        assignmentRepository.save(assignment);
    }

    @Override
    public List<ScheduleVehicleAssignmentDto> getAssignmentsBySchedule(UUID scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new NotFoundException("Route schedule not found with ID: " + scheduleId);
        }

        return assignmentRepository.findByRouteScheduleId(scheduleId).stream()
                .map(mapper::assignmentToDto)
                .map(this::enrichAssignmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleVehicleAssignmentDto> getAssignmentsByVehicle(UUID vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new NotFoundException("Vehicle not found with ID: " + vehicleId);
        }

        return assignmentRepository.findByVehicleId(vehicleId).stream()
                .map(mapper::assignmentToDto)
                .map(this::enrichAssignmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleVehicleAssignmentDto> getAssignmentsByDriver(UUID driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new NotFoundException("Driver not found with ID: " + driverId);
        }

        return assignmentRepository.findByDriverId(driverId).stream()
                .map(mapper::assignmentToDto)
                .map(this::enrichAssignmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleVehicleAssignmentDto> getAssignmentsByDate(LocalDate date) {
        return assignmentRepository.findByAssignmentDateAndIsActiveTrue(date).stream()
                .map(mapper::assignmentToDto)
                .map(this::enrichAssignmentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleVehicleAssignmentDto> getAllActiveAssignments() {
        List<ScheduleVehicleAssignment> assignments = assignmentRepository.findByIsActiveTrueAndUnassignedAtIsNull();
        if(assignments.isEmpty()){
            throw new NotFoundException("No active assignments found");
        }
        return assignments.stream().map(a ->{
            ScheduleVehicleAssignmentDto assignmentDto = ScheduleVehicleAssignmentMapper.INSTANCE.assignmentToDto(a);
            Vehicle vehicle = vehicleRepository.findById(a.getVehicleId()).orElse(null);
            RouteSchedule routeSchedule = scheduleRepository.findById(a.getRouteScheduleId()).orElse(null);
            if (routeSchedule != null) {
                Route route = routeRepository.findById(routeSchedule.getRouteId()).orElse(null);
                if(route != null) {
                    assignmentDto.setRouteCode(route.getRouteCode());
                    assignmentDto.setRouteName(route.getRouteName());
                    assignmentDto.setActive(a.getUnassignedAt()==null);
                    assignmentDto.setRouteScheduleDto(RouteScheduleMapper.INSTANCE.routeScheduleToDto(routeSchedule));
                }
            }
          return assignmentDto;
        }).collect(Collectors.toList());
    }

    @Override
    public ScheduleVehicleAssignmentDto getAssignmentById(UUID assignmentId) {
        ScheduleVehicleAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with ID: " + assignmentId));

        return enrichAssignmentDto(mapper.assignmentToDto(assignment));
    }

    @Override
    public List<ScheduleVehicleAssignmentDto> getAssignmentsByRouteAndDay(UUID routeId, String dayOfWeek) {
        // Find schedules for this route and day
        List<RouteSchedule> schedules = scheduleRepository.findAll().stream()
                .filter(s -> s.getRouteId().equals(routeId) && s.getDayOfWeek().name().equals(dayOfWeek))
                .collect(Collectors.toList());

        if (schedules.isEmpty()) {
            return List.of();
        }

        // Get all assignments for these schedules
        List<ScheduleVehicleAssignmentDto> assignments = new ArrayList<>();
        for (RouteSchedule schedule : schedules) {
            List<ScheduleVehicleAssignmentDto> scheduleAssignments = assignmentRepository.findByRouteScheduleId(schedule.getId())
                    .stream()
                    .map(mapper::assignmentToDto)
                    .map(this::enrichAssignmentDto)
                    .collect(Collectors.toList());

            assignments.addAll(scheduleAssignments);
        }

        return assignments;
    }

    // Helper to enrich DTOs with additional information from related entities
    private ScheduleVehicleAssignmentDto enrichAssignmentDto(ScheduleVehicleAssignmentDto dto) {
        RouteSchedule schedule = scheduleRepository.findById(dto.getRouteScheduleId()).orElse(null);
        if (schedule != null) {
            dto.setDayOfWeek(schedule.getDayOfWeek().name());
            dto.setStartTime(schedule.getStartTime().toString());
            dto.setEndTime(schedule.getEndTime().toString());
        }
        return dto;
    }
}