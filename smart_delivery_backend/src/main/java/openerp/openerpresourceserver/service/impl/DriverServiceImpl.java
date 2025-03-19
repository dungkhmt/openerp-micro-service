package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.OrderSummaryDTO;
import openerp.openerpresourceserver.dto.RouteVehicleDetailDto;
import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.entity.Driver;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.RouteVehicle;
import openerp.openerpresourceserver.entity.Vehicle;
import openerp.openerpresourceserver.entity.VehicleDriver;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.mapper.VehicleMapper;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.DriverService;
import openerp.openerpresourceserver.service.MiddleMileOrderService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepo driverRepo;
    private final VehicleDriverRepository vehicleDriverRepo;
    private final VehicleRepository vehicleRepo;
    private final RouteVehicleRepository routeVehicleRepo;
    private final OrderRepo orderRepo;
    private final MiddleMileOrderService middleMileOrderService;
    private final VehicleMapper vehicleMapper = VehicleMapper.INSTANCE;

    @Override
    public VehicleDto getDriverVehicleByUsername(String username) {
        // Find driver by username
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Find vehicle assigned to driver
        VehicleDriver vehicleDriver = vehicleDriverRepo.findByDriverIdAndUnassignedAtIsNull(driver.getId());
        if (vehicleDriver == null) {
            throw new NotFoundException("No vehicle assigned to driver: " + username);
        }

        // Get vehicle details
        Vehicle vehicle = vehicleRepo.findById(vehicleDriver.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle not found with ID: " + vehicleDriver.getVehicleId()));

        return vehicleMapper.vehicleToVehicleDto(vehicle);
    }

    @Override
    public List<RouteVehicleDetailDto> getDriverRoutesByUsername(String username) {
        // Find driver by username
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Find vehicle assigned to driver
        VehicleDriver vehicleDriver = vehicleDriverRepo.findByDriverIdAndUnassignedAtIsNull(driver.getId());
        if (vehicleDriver == null) {
            throw new NotFoundException("No vehicle assigned to driver: " + username);
        }

        // Get routes assigned to this vehicle
        return routeVehicleRepo.findDetailByVehicleId(vehicleDriver.getVehicleId());
    }

    @Override
    public List<Order> getOrdersForRouteVehicle(UUID routeVehicleId) {
        RouteVehicle routeVehicle = routeVehicleRepo.findById(routeVehicleId)
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found with ID: " + routeVehicleId));

        return middleMileOrderService.getOrdersByTrip(routeVehicleId);
    }

    @Override
    public List<OrderSummaryDTO> getPendingPickupOrdersForDriver(String username, UUID hubId) {
        // Find driver and vehicle
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        VehicleDriver vehicleDriver = vehicleDriverRepo.findByDriverIdAndUnassignedAtIsNull(driver.getId());
        if (vehicleDriver == null) {
            throw new NotFoundException("No vehicle assigned to driver: " + username);
        }

        // Get orders at the hub that are ready for pickup (COLLECTED_HUB status)
        // and are not already assigned to a vehicle
        List<Order> pendingOrders = orderRepo.findAll().stream()
                .filter(order ->
                        order.getOriginHubId() != null &&
                                order.getOriginHubId().equals(hubId) &&
                                order.getStatus() == OrderStatus.COLLECTED_HUB &&
                                order.getVehicleId() == null)
                .collect(Collectors.toList());

        return pendingOrders.stream()
                .map(OrderSummaryDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void pickupOrders(String username, List<UUID> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            throw new IllegalArgumentException("No orders provided for pickup");
        }

        // Find driver and vehicle
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        VehicleDriver vehicleDriver = vehicleDriverRepo.findByDriverIdAndUnassignedAtIsNull(driver.getId());
        if (vehicleDriver == null) {
            throw new NotFoundException("No vehicle assigned to driver: " + username);
        }

        Vehicle vehicle = vehicleRepo.findById(vehicleDriver.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle not found with ID: " + vehicleDriver.getVehicleId()));

        // Update orders status and assign to driver's vehicle
        List<Order> orders = new ArrayList<>();
        for (UUID orderId : orderIds) {
            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

            // Verify order is in correct state
            if (order.getStatus() != OrderStatus.COLLECTED_HUB) {
                throw new IllegalStateException("Order is not in COLLECTED_HUB state: " + orderId);
            }

            // Update order
            order.setStatus(OrderStatus.DELIVERING);
            order.setVehicleId(vehicle.getVehicleId());
            order.setVehicleType(vehicle.getVehicleType());
            order.setVehicleLicensePlate(vehicle.getPlateNumber());
            order.setDriverId(driver.getId());
            order.setDriverName(driver.getName());

            orders.add(order);
        }

        // Update vehicle status if needed
        if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {
            vehicle.setStatus(VehicleStatus.TRANSITING);
            vehicleRepo.save(vehicle);
        }

        orderRepo.saveAll(orders);
    }

    @Override
    @Transactional
    public void deliverOrders(String username, List<UUID> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            throw new IllegalArgumentException("No orders provided for delivery");
        }

        // Find driver
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Update orders status
        List<Order> orders = new ArrayList<>();
        for (UUID orderId : orderIds) {
            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

            // Verify order is assigned to this driver
            if (order.getDriverId() == null || !order.getDriverId().equals(driver.getId())) {
                throw new IllegalStateException("Order is not assigned to this driver: " + orderId);
            }

            // Verify order is in correct state
            if (order.getStatus() != OrderStatus.DELIVERING) {
                throw new IllegalStateException("Order is not in DELIVERING state: " + orderId);
            }

            // Update order
            order.setStatus(OrderStatus.DELIVERED);
            orders.add(order);
        }

        orderRepo.saveAll(orders);
    }

    @Override
    @Transactional
    public void updateOrderStatus(String username, UUID orderId, OrderStatus status) {
        // Find driver
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Find order
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        // Verify order is assigned to this driver
        if (order.getDriverId() == null || !order.getDriverId().equals(driver.getId())) {
            throw new IllegalStateException("Order is not assigned to this driver: " + orderId);
        }

        // Verify status transition is valid
        if (!OrderStatus.isValidTransition(order.getStatus(), status)) {
            throw new IllegalStateException("Invalid status transition from " + order.getStatus() + " to " + status);
        }

        // Update order
        order.setStatus(status);
        orderRepo.save(order);
    }

    @Override
    public List<OrderSummaryDTO> getCurrentOrdersForDriver(String username) {
        // Find driver
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Find orders assigned to this driver
        List<Order> driverOrders = orderRepo.findAll().stream()
                .filter(order ->
                        order.getDriverId() != null &&
                                order.getDriverId().equals(driver.getId()) &&
                                (order.getStatus() == OrderStatus.DELIVERING || order.getStatus() == OrderStatus.DELIVERED))
                .collect(Collectors.toList());

        return driverOrders.stream()
                .map(OrderSummaryDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void completeTrip(String username, UUID routeVehicleId) {
        // Find driver
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Find vehicle assigned to driver
        VehicleDriver vehicleDriver = vehicleDriverRepo.findByDriverIdAndUnassignedAtIsNull(driver.getId());
        if (vehicleDriver == null) {
            throw new NotFoundException("No vehicle assigned to driver: " + username);
        }

        // Find route vehicle
        RouteVehicle routeVehicle = routeVehicleRepo.findById(routeVehicleId)
                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found with ID: " + routeVehicleId));

        // Verify route vehicle is assigned to driver's vehicle
        if (!routeVehicle.getVehicleId().equals(vehicleDriver.getVehicleId())) {
            throw new IllegalStateException("Route vehicle is not assigned to driver's vehicle");
        }

        // Complete the trip
        middleMileOrderService.completeTrip(routeVehicleId);
    }
}