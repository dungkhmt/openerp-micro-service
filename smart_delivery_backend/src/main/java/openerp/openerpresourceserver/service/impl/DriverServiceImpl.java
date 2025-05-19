package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.OrderForTripDto;
import openerp.openerpresourceserver.dto.OrderSuggestionDto;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.*;
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
    public List<OrderForTripDto> getPendingPickupOrdersForDriver(String username, UUID tripId) {
        // Find driver and vehicle
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }

        VehicleDriver vehicleDriver = vehicleDriverRepo.findByDriverIdAndUnassignedAtIsNull(driver.getId());
        if (vehicleDriver == null) {
            throw new NotFoundException("No vehicle assigned to driver: " + username);
        }
        List<TripOrder> tripOrders = tripOrderRepository.findAllByTripId(tripId);
        List<UUID> tripOrderIds = tripOrders.stream().map(TripOrder::getOrderId).toList();
        List<Order> orders = orderRepo.findAllById(tripOrderIds);

        return orders.stream().map(order -> {
            OrderForTripDto orderForTripDto = new OrderForTripDto(order);
            Sender sender = senderRepo.findById(order.getSenderId()).orElseThrow(() -> new NotFoundException("sender not found " + order.getSenderId()));
            Recipient recipient = recipientRepo.findById(order.getRecipientId()).orElseThrow(() -> new NotFoundException("sender not found " + order.getSenderId()));
            orderForTripDto.setId(order.getId());
            orderForTripDto.setSenderName(order.getSenderName());
            orderForTripDto.setRecipientName(order.getRecipientName());
            orderForTripDto.setSenderPhone(sender.getPhone());
            orderForTripDto.setRecipientPhone(recipient.getPhone());
            orderForTripDto.setSenderAddress(sender.getAddress());
            orderForTripDto.setRecipientAddress(recipient.getAddress());
            return orderForTripDto;
        }).collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void pickupOrders(String username, List<UUID> orderIds, UUID tripId) {
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
        List<TripOrder> tripOrders = new ArrayList<>();
        for (UUID orderId : orderIds) {
            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Order item not found with ID: " + orderId));

            // Verify order is in correct state
            if (order.getStatus() != OrderStatus.CONFIRMED_OUT) {
                throw new IllegalStateException("Order is not in CONFIRMED_OUT state: " + orderId);
            }

            // Update order
            order.setStatus(OrderStatus.DELIVERING);

            orders.add(order);
            TripOrder tripOrder = tripOrderRepository.findTopByOrderIdOrderByCreatedAtDesc(orderId);
            tripOrder.setStatus("DELIVERING");
            tripOrders.add(tripOrder);


            // Update vehicle status if needed
            if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {
                vehicle.setStatus(VehicleStatus.TRANSITING);
                vehicleRepo.save(vehicle);
            }

            orderRepo.saveAll(orders);
            tripOrderRepository.saveAll(tripOrders );
        }
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
        List<TripOrder> tripOrders = new ArrayList<>();

        for (UUID orderId : orderIds) {
            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

            TripOrder tripOrder = tripOrderRepository.findTopByOrderIdOrderByCreatedAtDesc(orderId);
            Trip trip = tripRepository.findById(tripOrder.getTripId()).orElseThrow(() -> new NotFoundException("Trip not found"));
            RouteSchedule routeSchedule = routeScheduleRepository.findById(trip.getRouteScheduleId()).orElseThrow(() -> new NotFoundException("Route schedule not found"));
            if (!trip.getDriverId().equals(driver.getId())) {
                throw new IllegalStateException("Order is not assigned to this driver: " + orderId);
            }
            // Verify order is assigned to this driver


            // Verify order is in correct state
            if (order.getStatus() != OrderStatus.DELIVERING) {
                throw new IllegalStateException("Order item is not in DELIVERING state: " + orderId);
            }

            // Update order
            order.setStatus(OrderStatus.DELIVERED);
            orders.add(order);
            tripOrder.setStatus("DELIVERED");
            tripOrders.add(tripOrder);
        }

        orderRepo.saveAll(orders);
        tripOrderRepository.saveAll(tripOrders);
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

//        // Verify order is assigned to this driver
//        if (order.getDriverId() == null || !order.getDriverId().equals(driver.getId())) {
//            throw new IllegalStateException("Order is not assigned to this driver: " + orderId);
//        }

        // Verify status transition is valid
        if (!OrderStatus.isValidTransition(order.getStatus(), status)) {
            throw new IllegalStateException("Invalid status transition from " + order.getStatus() + " to " + status);
        }

        // Update order
        order.setStatus(status);
        orderRepo.save(order);
    }

    @Override
    public List<OrderForTripDto> getCurrentOrderItemsForDriver(String username, UUID tripId) {
        // Find driver
        Driver driver = driverRepo.findByUsername(username);
        if (driver == null) {
            throw new NotFoundException("Driver not found with username: " + username);
        }
        List<TripOrder> tripOrders = tripOrderRepository.findAllByTripId(tripId);
        List<UUID> orderIds = tripOrders.stream().map(TripOrder::getOrderId).collect(Collectors.toList());
        List<Order> driverOrders = orderRepo.findAllByIdAndStatus(orderIds, OrderStatus.DELIVERING);

        RouteStop stop = tripService.getCurrentRouteStop(tripId);
        if (stop == null) {
            throw new IllegalStateException("No current stop found for trip: " + tripId);
        }
        System.out.println("hub id: " + stop.getHubId());
        driverOrders = driverOrders.stream().filter(o -> {
            return o.getFinalHubId().equals(stop.getHubId());
        }).toList();
        return driverOrders.stream()
                .map(this::convertOrderToDtoForTrip)
                .collect(Collectors.toList());
    }

    private OrderForTripDto convertOrderToDtoForTrip(Order order) {
        OrderForTripDto orderForTripDto = new OrderForTripDto(order);
        Sender sender = senderRepo.findById(order.getSenderId()).orElseThrow(() -> new NotFoundException("sender not found " + order.getSenderId()));
        Recipient recipient = recipientRepo.findById(order.getRecipientId()).orElseThrow(() -> new NotFoundException("sender not found " + order.getSenderId()));
        orderForTripDto.setId(order.getId());
        orderForTripDto.setSenderName(order.getSenderName());
        orderForTripDto.setRecipientName(order.getRecipientName());
        orderForTripDto.setSenderPhone(sender.getPhone());
        orderForTripDto.setRecipientPhone(recipient.getPhone());
        orderForTripDto.setSenderAddress(sender.getAddress());
        orderForTripDto.setRecipientAddress(recipient.getAddress());
        return orderForTripDto;
    }
    /**
     * Get suggested order items for a trip based on route and vehicle capacity
     * Priority: older orders first, within capacity limits
     */
    public List<OrderSuggestionDto> getSuggestedOrderItemsForTrip(UUID tripId) {
        log.info("Getting suggested order items for trip: {}", tripId);

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));

        RouteSchedule routeSchedule = routeScheduleRepository.findById(trip.getRouteScheduleId())
                .orElseThrow(() -> new NotFoundException("Route schedule not found"));

        // Get vehicle for capacity checking
        Vehicle vehicle = vehicleRepo.findById(trip.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));

        // Get all route stops in order (pickup + delivery destinations)
        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequence(routeSchedule.getRouteId());

        // Calculate current trip load
        double currentWeight = getCurrentTripWeight(tripId);
        double currentVolume = getCurrentTripVolume(tripId);

        List<OrderSuggestionDto> suggestions = new ArrayList<>();

        // For each route stop, find orders that need to be delivered there
        for (RouteStop stop : routeStops) {
            List<Order> ordersForStop = getOrdersForRouteStop(stop.getHubId(), trip.getDate());

            // Sort orders by creation time (oldest first)
            ordersForStop.sort(Comparator.comparing(o -> {
                Order order = orderRepo.findById(o.getId()).orElse(null);
                return order != null ? order.getCreatedAt() : new Date();
            }));

            for (Order order : ordersForStop) {
                // Check if adding this order would exceed capacity
                if (canFitInVehicle(order, vehicle, currentWeight, currentVolume)) {
                    OrderSuggestionDto suggestion = createSimpleSuggestion(order, stop, vehicle);
                    suggestions.add(suggestion);

                    // Update running totals for next calculations
                    currentWeight += order.getWeight();
                    currentVolume += order.getVolume();

                    log.debug("Added order {} to suggestions (weight: {}, volume: {})",
                            order.getId(), order.getWeight());
                } else {
                    log.debug("Skipped order {} - exceeds vehicle capacity", order.getId());
                    // Stop adding more orders from this stop if capacity is reached
                    break;
                }
            }
        }

        log.info("Found {} order suggestions for trip {} within capacity limits",
                suggestions.size(), tripId);

        return suggestions;
    }

    /**
     * Get available orders for a specific route stop
     */
    private List<Order> getOrdersForRouteStop(UUID hubId, LocalDate date) {
        // Get orders that are ready for pickup/delivery at this hub on this date
        return orderRepo.findByStatusAndFinalHubId(
                OrderStatus.COLLECTED_HUB,
                hubId

        );
    }

    /**
     * Check if an order can fit in the vehicle without exceeding capacity
     */
    private boolean canFitInVehicle(Order orderItem, Vehicle vehicle, double currentWeight, double currentVolume) {
        // Check weight capacity
        if (vehicle.getWeightCapacity() != null) {
            if (currentWeight + orderItem.getWeight() > vehicle.getWeightCapacity()) {
                return false;
            }
        }

        // Check volume capacity
        if (vehicle.getVolumeCapacity() != null) {
            if (currentVolume + orderItem.getHeight() * orderItem.getLength() * orderItem.getWidth() > vehicle.getVolumeCapacity()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Create simple suggestion with basic info
     */
    private OrderSuggestionDto createSimpleSuggestion(Order order, RouteStop stop, Vehicle vehicle) {
        OrderSuggestionDto suggestion = new OrderSuggestionDto();

        // Get order details

        // Basic information
        suggestion.setOrderId(order.getId());
        suggestion.setWeight(order.getWeight());
        suggestion.setVolume(order.getVolume());

        // Route information
        suggestion.setHubId(stop.getHubId());
        suggestion.setStopSequence(stop.getStopSequence());

        // Priority based on creation time (older = higher priority)
        int priority = calculateTimePriority(order.getCreatedAt());
        suggestion.setPriority(priority);

        // Simple fit indication
        suggestion.setFitScore(100); // If we get here, it means it fits

        // Basic order info for display
        suggestion.setSenderName(order.getSenderName());
        suggestion.setRecipientName(order.getRecipientName());
        suggestion.setCurrentStatus(order.getStatus().name());

        return suggestion;
    }

    /**
     * Calculate priority based on creation time - older orders get higher priority
     */
    private int calculateTimePriority(Timestamp createdAt) {
        long hoursOld = java.time.temporal.ChronoUnit.HOURS.between(createdAt.toInstant(), Instant.now());

        // Base priority 50, add 1 point per hour up to max 100
        int priority = (int) Math.min(50 + hoursOld, 100);

        return priority;
    }

    /**
     * Get current total weight of already assigned orders in the trip
     */
    private double getCurrentTripWeight(UUID tripId) {
        List<TripItem> tripItems = tripItemRepository.findAllByTripId(tripId);
        return tripItems.stream()
                .filter(tripItem -> !"CANCELLED".equals(tripItem.getStatus()))
                .mapToDouble(tripItem -> {
                    OrderItem orderItem = orderItemRepo.findById(tripItem.getOrderItemId()).orElse(null);
                    return orderItem != null ? orderItem.getWeight() : 0.0;
                })
                .sum();
    }

    /**
     * Get current total volume of already assigned orders in the trip
     */
    private double getCurrentTripVolume(UUID tripId) {
        List<TripItem> tripItems = tripItemRepository.findAllByTripId(tripId);
        return tripItems.stream()
                .filter(tripItem -> !"CANCELLED".equals(tripItem.getStatus()))
                .mapToDouble(tripItem -> {
                    OrderItem orderItem = orderItemRepo.findById(tripItem.getOrderItemId()).orElse(null);
                    return orderItem != null ? orderItem.getHeight()*orderItem.getLength()*orderItem.getWidth() : 0.0;
                })
                .sum();
    }
}