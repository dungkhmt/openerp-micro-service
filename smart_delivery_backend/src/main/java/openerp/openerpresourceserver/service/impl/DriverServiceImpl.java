package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.OrderItemForTripDto;
import openerp.openerpresourceserver.dto.OrderSummaryDTO;
import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.OrderItemStatus;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.mapper.VehicleMapper;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.DriverService;
import openerp.openerpresourceserver.service.MiddleMileOrderService;
import openerp.openerpresourceserver.service.OrderService;
import openerp.openerpresourceserver.service.TripService;
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
    private final RouteStopRepository routeStopRepository;
    private final OrderRepo orderRepo;
    private final OrderService orderService;
    private final RouteRepository routeRepository;
    private final MiddleMileOrderService middleMileOrderService;
    private final VehicleMapper vehicleMapper = VehicleMapper.INSTANCE;
    private final TripOrderRepository tripOrderRepository;
    private final TripRepository tripRepository;
    private final TripService tripService;
    private final TripItemRepository tripItemRepository;
    private final OrderItemRepo orderItemRepo;
    private final SenderRepo senderRepo;
    private final RecipientRepo recipientRepo;
    private final RouteScheduleRepository routeScheduleRepository;
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
    public List<OrderItemForTripDto> getPendingPickupOrderItemsForDriver(String username, UUID tripId) {
        // Find driver and vehicle
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        VehicleDriver vehicleDriver = vehicleDriverRepo.findByDriverIdAndUnassignedAtIsNull(driver.getId());
        if (vehicleDriver == null) {
            throw new NotFoundException("No vehicle assigned to driver: " + username);
        }
        List<TripItem> tripItems = tripItemRepository.findAllByTripId(tripId);
        List<UUID> tripItemIds = tripItems.stream().map(TripItem::getOrderItemId).collect(Collectors.toList());
        List<OrderItem> orderItems = orderItemRepo.findAllById(tripItemIds);

        return orderItems.stream().map(o ->{
            OrderItemForTripDto orderItemForTripDto = new OrderItemForTripDto(o);
            Order order = orderRepo.findById(o.getOrderId()).orElseThrow(() -> new NotFoundException("order not found " + o.getOrderId()));
            Sender sender = senderRepo.findById(order.getSenderId()).orElseThrow(() -> new NotFoundException("sender not found " + order.getSenderId()));
            Recipient recipient = recipientRepo.findById(order.getRecipientId()).orElseThrow(() -> new NotFoundException("sender not found " + order.getSenderId()));
            orderItemForTripDto.setOrderId(order.getId());
            orderItemForTripDto.setSenderName(order.getSenderName());
            orderItemForTripDto.setRecipientName(order.getRecipientName());
            orderItemForTripDto.setSenderPhone(sender.getPhone());
            orderItemForTripDto.setRecipientPhone(recipient.getPhone());
            orderItemForTripDto.setSenderAddress(sender.getAddress());
            orderItemForTripDto.setRecipientAddress(recipient.getAddress());
            return orderItemForTripDto;
        }).collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void pickupOrders(String username, List<UUID> orderItemIds, UUID tripId) {
        if (orderItemIds == null || orderItemIds.isEmpty()) {
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
        List<OrderItem> orderItems = new ArrayList<>();
        List<TripItem> tripItems = new ArrayList<>();
        for (UUID orderItemId : orderItemIds) {
            OrderItem orderItem = orderItemRepo.findById(orderItemId)
                    .orElseThrow(() -> new NotFoundException("Order item not found with ID: " + orderItemId));

            // Verify order is in correct state
            if (orderItem.getStatus() != OrderItemStatus.COLLECTED_HUB) {
                throw new IllegalStateException("Order is not in COLLECTED_HUB state: " + orderItemId);
            }

            // Update order
            orderItem.setStatus(OrderItemStatus.DELIVERING);

            orderItems.add(orderItem);

            tripItems.add(new TripItem().builder()
                    .tripId(tripId)
                    .orderItemId(orderItemId)
                    .sequenceNumber(orderItems.indexOf(orderItem))
                    .delivered(false)
                    .isPickup(false)
                    .build());


        // Update vehicle status if needed
        if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {
            vehicle.setStatus(VehicleStatus.TRANSITING);
            vehicleRepo.save(vehicle);
        }

        orderItemRepo.saveAll(orderItems);
        tripItemRepository.saveAll(tripItems);
    }
        }

    @Override
    @Transactional
    public void deliverOrderItems(String username, List<UUID> orderItemIds) {
        if (orderItemIds == null || orderItemIds.isEmpty()) {
            throw new IllegalArgumentException("No orders provided for delivery");
        }

        // Find driver
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        // Update orders status
        List<OrderItem> orderItems = new ArrayList<>();
        List<TripItem> tripItems = new ArrayList<>();

        for (UUID orderItemId : orderItemIds) {
            OrderItem orderItem = orderItemRepo.findById(orderItemId)
                    .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderItemId));

            TripItem tripItem = tripItemRepository.findByOrderItemId(orderItemId);
            Trip trip = tripRepository.findById(tripItem.getTripId()).orElseThrow(() -> new NotFoundException("Trip not found"));
            RouteSchedule routeSchedule = routeScheduleRepository.findById(trip.getRouteScheduleId()).orElseThrow(() -> new NotFoundException("Route schedule not found"));
            if(!trip.getDriverId().equals(driver.getId())) {
                throw new IllegalStateException("Order is not assigned to this driver: " + orderItemId);
            }
            // Verify order is assigned to this driver


            // Verify order is in correct state
            if (orderItem.getStatus() != OrderItemStatus.DELIVERING) {
                throw new IllegalStateException("Order item is not in DELIVERING state: " + orderItemId);
            }

            // Update order
            orderItem.setStatus(OrderItemStatus.DELIVERED);
            orderItems.add(orderItem);
            tripItem.setStatus("COMPLETED");
            tripItems.add(tripItem);
        }

        orderItemRepo.saveAll(orderItems);
        tripItemRepository.saveAll(tripItems);
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
    public List<OrderItemForTripDto> getCurrentOrderItemsForDriver(String username, UUID tripId) {
        // Find driver
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }
        List<TripItem> tripItems = tripItemRepository.findAllByTripId(tripId);
        List<UUID> orderItemIds = tripItems.stream().map(TripItem::getOrderItemId).collect(Collectors.toList());
        List<OrderItem> driverOrderItems = orderItemRepo.findAllByIdAndStatus(orderItemIds, OrderItemStatus.DELIVERING);
        RouteStop stop = tripService.getCurrentRouteStop(tripId);
        if(stop == null){
            throw new IllegalStateException("No current stop found for trip: " + tripId);
        }
        System.out.println("hub id: " + stop.getHubId());
        driverOrderItems = driverOrderItems.stream().filter(o -> {
            Order order = orderRepo.findById(o.getOrderId()).orElseThrow(() -> new NotFoundException("order not found " + o.getOrderId()));
            return order.getFinalHubId().equals(stop.getHubId());
        }).toList();
        return driverOrderItems.stream()
                .map(this::convertOrderItemToDtoForTrip)
                .collect(Collectors.toList());
    }

    private OrderItemForTripDto convertOrderItemToDtoForTrip(OrderItem orderItem){
        OrderItemForTripDto orderItemForTripDto = new OrderItemForTripDto(orderItem);
        Order order = orderRepo.findById(orderItem.getOrderId()).orElseThrow(() -> new NotFoundException("order not found " + orderItem.getOrderId()));
        Sender sender = senderRepo.findById(order.getSenderId()).orElseThrow(() -> new NotFoundException("sender not found " + order.getSenderId()));
        Recipient recipient = recipientRepo.findById(order.getRecipientId()).orElseThrow(() -> new NotFoundException("sender not found " + order.getSenderId()));
        orderItemForTripDto.setOrderId(order.getId());
        orderItemForTripDto.setSenderName(order.getSenderName());
        orderItemForTripDto.setRecipientName(order.getRecipientName());
        orderItemForTripDto.setSenderPhone(sender.getPhone());
        orderItemForTripDto.setRecipientPhone(recipient.getPhone());
        orderItemForTripDto.setSenderAddress(sender.getAddress());
        orderItemForTripDto.setRecipientAddress(recipient.getAddress());
        return orderItemForTripDto;
    }

}