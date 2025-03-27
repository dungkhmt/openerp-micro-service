package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.TripAssignmentService;
import openerp.openerpresourceserver.utils.DistanceCalculator.HaversineDistanceCalculator;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the TripAssignmentService interface.
 * Uses a greedy algorithm to assign orders to trips, optimizing for:
 * 1. Minimum total travel distance
 * 2. Maximum vehicle capacity utilization
 * 3. Load balancing across vehicles
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TripAssignmentServiceImpl implements TripAssignmentService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final TripRepository tripRepository;
    private final TripOrderRepository tripOrderRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleDriverRepository vehicleDriverRepository;
    private final HubRepo hubRepo;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final DriverRepo driverRepo;

    // Maximum weight capacity per vehicle (in kg)
    private static final double MAX_WEIGHT_CAPACITY = 500.0;
    // Target capacity utilization percentage (0.0-1.0)
    private static final double TARGET_UTILIZATION = 0.8;

    @Override
    @Transactional
    public int assignMorningTrips(UUID hubId) {
        log.info("Running morning trip assignment for hub {}", hubId);
        return assignTripsForTimeOfDay(hubId, LocalTime.of(8, 0));
    }

    @Override
    @Transactional
    public int assignEveningTrips(UUID hubId) {
        log.info("Running evening trip assignment for hub {}", hubId);
        return assignTripsForTimeOfDay(hubId, LocalTime.of(17, 0));
    }

    /**
     * Assigns orders to trips for a specific time of day
     */
    private int assignTripsForTimeOfDay(UUID hubId, LocalTime scheduledTime) {
        // Verify hub exists
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new NotFoundException("Hub not found with ID: " + hubId));

        // Get all orders at the hub that need to be assigned (COLLECTED_HUB status)
        List<Order> pendingOrders = orderRepo.findAll().stream()
                .filter(order -> order.getOriginHubId() != null &&
                        order.getOriginHubId().equals(hubId) &&
                        order.getStatus() == OrderStatus.COLLECTED_HUB)
                .collect(Collectors.toList());

        if (pendingOrders.isEmpty()) {
            log.info("No pending orders for hub {}", hubId);
            return 0;
        }

        // Get all available vehicles and drivers
        List<Vehicle> availableVehicles = vehicleRepository.findAll().stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                .collect(Collectors.toList());

        if (availableVehicles.isEmpty()) {
            log.warn("No available vehicles for assignment at hub {}", hubId);
            return 0;
        }

        // Create planned trips for available vehicles
        List<Trip> plannedTrips = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDateTime scheduledDateTime = LocalDateTime.of(today, scheduledTime);
        Instant scheduledInstant = scheduledDateTime.toInstant(ZoneOffset.UTC);

        for (Vehicle vehicle : availableVehicles) {
            // Find driver for vehicle
            VehicleDriver vehicleDriver = vehicleDriverRepository.findByVehicleIdAndUnassignedAtIsNull(vehicle.getVehicleId());
            if (vehicleDriver == null) {
                continue; // Skip vehicles without drivers
            }

            // Find routes that include this hub
            List<Route> availableRoutes = routeRepository.findByHubId(hubId);
            if (availableRoutes.isEmpty()) {
                continue; // Skip if no routes available
            }

            // Use the first available route (this could be enhanced with better route selection)
            Route selectedRoute = availableRoutes.get(0);

            // Create a new trip
            Trip newTrip = Trip.builder()
                    .routeScheduleId(selectedRoute.getRouteId())
                    .driverId(vehicleDriver.getDriverId())
                    .startTime(scheduledInstant)
                    .status("PLANNED")
                    .currentStopIndex(0)
                    .ordersPickedUp(0)
                    .ordersDelivered(0)
                    .build();

            plannedTrips.add(tripRepository.save(newTrip));
        }

        if (plannedTrips.isEmpty()) {
            log.warn("Could not create any planned trips for hub {}", hubId);
            return 0;
        }

        // Use greedy algorithm to assign orders to trips
        return assignOrdersToTripsGreedy(pendingOrders, plannedTrips, hub);
    }

    /**
     * Greedy algorithm for assigning orders to trips
     * The algorithm prioritizes:
     * 1. Orders with nearby destinations to each other
     * 2. Maximum vehicle capacity utilization without exceeding limits
     * 3. Load balancing across available vehicles
     */
    private int assignOrdersToTripsGreedy(List<Order> orders, List<Trip> trips, Hub sourceHub) {
        if (orders.isEmpty() || trips.isEmpty()) {
            return 0;
        }

        // Calculate total order weight for each trip
        Map<UUID, Double> tripWeights = new HashMap<>();
        for (Trip trip : trips) {
            tripWeights.put(trip.getId(), 0.0);
        }

        // Track assigned orders
        Set<UUID> assignedOrderIds = new HashSet<>();
        int totalAssigned = 0;

        // Sort orders by weight (descending) to assign larger items first
        orders.sort(Comparator.comparing(Order::getTotalPrice, Comparator.nullsLast(Comparator.reverseOrder())));

        // Cluster orders by destination proximity
        Map<UUID, List<Order>> destinationClusters = clusterOrdersByDestination(orders);

        // Assign clusters to trips
        for (List<Order> cluster : destinationClusters.values()) {
            // Sort trips by current weight (ascending) to balance load
            List<Trip> sortedTrips = trips.stream()
                    .sorted(Comparator.comparing(trip -> tripWeights.get(trip.getId())))
                    .collect(Collectors.toList());

            // Try to assign the entire cluster to one trip if possible
            double clusterWeight = calculateClusterWeight(cluster);
            boolean clusterAssigned = false;

            for (Trip trip : sortedTrips) {
                double currentTripWeight = tripWeights.get(trip.getId());
                if (currentTripWeight + clusterWeight <= MAX_WEIGHT_CAPACITY) {
                    // Assign all orders in cluster to this trip
                    assignClusterToTrip(cluster, trip);

                    // Update trip weight
                    tripWeights.put(trip.getId(), currentTripWeight + clusterWeight);

                    // Mark orders as assigned
                    cluster.forEach(order -> assignedOrderIds.add(order.getId()));
                    totalAssigned += cluster.size();

                    clusterAssigned = true;
                    break;
                }
            }

            // If couldn't assign whole cluster, assign individual orders
            if (!clusterAssigned) {
                for (Order order : cluster) {
                    if (assignedOrderIds.contains(order.getId())) {
                        continue; // Skip already assigned orders
                    }

                    // Calculate order weight
                    double orderWeight = calculateOrderWeight(order);

                    // Find best trip for this order
                    Trip bestTrip = null;
                    double bestScore = -1;

                    for (Trip trip : sortedTrips) {
                        double currentTripWeight = tripWeights.get(trip.getId());
                        if (currentTripWeight + orderWeight <= MAX_WEIGHT_CAPACITY) {
                            // Calculate a score for this assignment based on capacity utilization
                            double newUtilization = (currentTripWeight + orderWeight) / MAX_WEIGHT_CAPACITY;
                            double utilizationScore = 1.0 - Math.abs(TARGET_UTILIZATION - newUtilization);

                            if (utilizationScore > bestScore) {
                                bestScore = utilizationScore;
                                bestTrip = trip;
                            }
                        }
                    }

                    // Assign order to best trip if found
                    if (bestTrip != null) {
                        assignOrderToTrip(order, bestTrip);
                        tripWeights.put(bestTrip.getId(), tripWeights.get(bestTrip.getId()) + orderWeight);
                        assignedOrderIds.add(order.getId());
                        totalAssigned++;
                    } else {
                        log.warn("Could not assign order {} to any trip due to capacity constraints", order.getId());
                    }
                }
            }
        }

        // Update order statuses for assigned orders
        for (UUID orderId : assignedOrderIds) {
            Order order = orderRepo.findById(orderId).orElse(null);
            if (order != null) {
                order.setStatus(OrderStatus.DELIVERING);
                orderRepo.save(order);
            }
        }

        log.info("Assigned {} orders to {} trips", totalAssigned, trips.size());
        return totalAssigned;
    }

    /**
     * Group orders into clusters by proximity of destinations
     */
    private Map<UUID, List<Order>> clusterOrdersByDestination(List<Order> orders) {
        Map<UUID, List<Order>> clusters = new HashMap<>();

        // Simple clustering: group by final hub ID
        for (Order order : orders) {
            UUID finalHubId = order.getFinalHubId();
            if (finalHubId != null) {
                clusters.computeIfAbsent(finalHubId, k -> new ArrayList<>()).add(order);
            } else {
                // For orders without final hub, put in separate clusters
                clusters.computeIfAbsent(order.getId(), k -> new ArrayList<>()).add(order);
            }
        }

        return clusters;
    }

    /**
     * Calculate the total weight of a cluster of orders
     */
    private double calculateClusterWeight(List<Order> cluster) {
        return cluster.stream()
                .mapToDouble(this::calculateOrderWeight)
                .sum();
    }

    /**
     * Calculate the weight of an order (estimate based on price if actual weight not available)
     */
    private double calculateOrderWeight(Order order) {
        // Use actual weight if available (from order items), otherwise estimate based on price
        // This is a simplification; in a real system, you would use actual weight data
        List<OrderItem> orderItems = orderItemRepo.findAllByOrderId(order.getId());

        return orderItems.size() != 0 ? orderItems.stream().mapToDouble(OrderItem::getWeight).sum(): 0; // Default 5kg if no price
    }

    /**
     * Assign a cluster of orders to a trip
     */
    private void assignClusterToTrip(List<Order> cluster, Trip trip) {
        // Sort orders within cluster by sequence for efficient delivery
        List<Order> sortedOrders = optimizeOrderSequence(cluster, trip);

        int sequence = 1;
        for (Order order : sortedOrders) {
            TripOrder tripOrder = TripOrder.builder()
                    .tripId(trip.getId())
                    .orderId(order.getId())
                    .sequenceNumber(sequence++)
                    .isPickup(true)
                    .isDelivery(true)
                    .status("PENDING")
                    .build();

            tripOrderRepository.save(tripOrder);

            // Update order with trip/vehicle info
            order.setRouteId(trip.getRouteScheduleId());
            order.setDriverId(trip.getDriverId());

            // Find driver name
            Driver driver = driverRepo.findById(trip.getDriverId()).orElse(null);
            if (driver != null) {
                order.setDriverName(driver.getName());
            }

            // Find vehicle info
            VehicleDriver vehicleDriver = vehicleDriverRepository.findByDriverIdAndUnassignedAtIsNull(trip.getDriverId());
            if (vehicleDriver != null) {
                Vehicle vehicle = vehicleRepository.findById(vehicleDriver.getVehicleId()).orElse(null);
                if (vehicle != null) {
                    order.setVehicleId(vehicle.getVehicleId());
                    order.setVehicleType(vehicle.getVehicleType());
                    order.setVehicleLicensePlate(vehicle.getPlateNumber());
                }
            }

            orderRepo.save(order);
        }
    }

    /**
     * Assign a single order to a trip
     */
    private void assignOrderToTrip(Order order, Trip trip) {
        // Get current highest sequence number
        List<TripOrder> existingOrders = tripOrderRepository.findByTripIdOrderBySequenceNumber(trip.getId());
        int nextSequence = existingOrders.isEmpty() ? 1 :
                existingOrders.stream().mapToInt(TripOrder::getSequenceNumber).max().orElse(0) + 1;

        TripOrder tripOrder = TripOrder.builder()
                .tripId(trip.getId())
                .orderId(order.getId())
                .sequenceNumber(nextSequence)
                .isPickup(true)
                .isDelivery(true)
                .status("PENDING")
                .build();

        tripOrderRepository.save(tripOrder);

        // Update order with trip/vehicle info (same as in assignClusterToTrip)
        order.setRouteId(trip.getRouteScheduleId());
        order.setDriverId(trip.getDriverId());

        // Find driver name
        Driver driver = driverRepo.findById(trip.getDriverId()).orElse(null);
        if (driver != null) {
            order.setDriverName(driver.getName());
        }

        // Find vehicle info
        VehicleDriver vehicleDriver = vehicleDriverRepository.findByDriverIdAndUnassignedAtIsNull(trip.getDriverId());
        if (vehicleDriver != null) {
            Vehicle vehicle = vehicleRepository.findById(vehicleDriver.getVehicleId()).orElse(null);
            if (vehicle != null) {
                order.setVehicleId(vehicle.getVehicleId());
                order.setVehicleType(vehicle.getVehicleType());
                order.setVehicleLicensePlate(vehicle.getPlateNumber());
            }
        }

        orderRepo.save(order);
    }

    /**
     * Optimize the sequence of orders within a trip to minimize total travel distance
     */
    private List<Order> optimizeOrderSequence(List<Order> orders, Trip trip) {
        if (orders.size() <= 1) {
            return new ArrayList<>(orders);
        }

        // Implement a basic nearest neighbor algorithm (greedy) for TSP
        List<Order> result = new ArrayList<>();
        Set<UUID> unvisited = orders.stream().map(Order::getId).collect(Collectors.toSet());

        // Start with a random order
        Order current = orders.get(0);
        result.add(current);
        unvisited.remove(current.getId());

        while (!unvisited.isEmpty()) {
            // Find nearest unvisited order
            Order nearest = null;
            double minDistance = Double.MAX_VALUE;

            for (Order order : orders) {
                if (!unvisited.contains(order.getId())) {
                    continue;
                }

                double distance = calculateDistance(current, order);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = order;
                }
            }

            if (nearest != null) {
                result.add(nearest);
                unvisited.remove(nearest.getId());
                current = nearest;
            } else {
                break; // Should not happen
            }
        }

        return result;
    }

    /**
     * Calculate distance between two orders (using their destination coordinates)
     */
    private double calculateDistance(Order order1, Order order2) {
        // Get recipient coordinates (simplified example)
        double lat1 = 0, lon1 = 0, lat2 = 0, lon2 = 0;

        // Try to use final hub coordinates if available
        if (order1.getFinalHubId() != null) {
            Hub hub1 = hubRepo.findById(order1.getFinalHubId()).orElse(null);
            if (hub1 != null) {
                lat1 = hub1.getLatitude();
                lon1 = hub1.getLongitude();
            }
        }

        if (order2.getFinalHubId() != null) {
            Hub hub2 = hubRepo.findById(order2.getFinalHubId()).orElse(null);
            if (hub2 != null) {
                lat2 = hub2.getLatitude();
                lon2 = hub2.getLongitude();
            }
        }

        // Calculate distance using Haversine formula
        return HaversineDistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);
    }

    @Override
    @Transactional
    public List<TripOrder> assignOrdersToTrip(UUID tripId, List<UUID> orderIds) {
        // Verify trip exists
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        // Verify orders exist
        List<Order> orders = orderRepo.findAllById(orderIds);
        if (orders.size() != orderIds.size()) {
            throw new NotFoundException("Some orders were not found");
        }

        // Optimize order sequence
        List<Order> optimizedOrders = optimizeOrderSequence(orders, trip);

        // Create TripOrder entities
        List<TripOrder> tripOrders = new ArrayList<>();
        int sequence = 1;

        for (Order order : optimizedOrders) {
            // Check if already assigned
            TripOrder existing = tripOrderRepository.findByTripIdAndOrderId(tripId, order.getId());
            if (existing != null) {
                continue; // Skip if already assigned
            }

            TripOrder tripOrder = TripOrder.builder()
                    .tripId(tripId)
                    .orderId(order.getId())
                    .sequenceNumber(sequence++)
                    .isPickup(true)
                    .isDelivery(true)
                    .status("PENDING")
                    .build();

            tripOrders.add(tripOrderRepository.save(tripOrder));

            // Update order status
            order.setStatus(OrderStatus.DELIVERING);
            order.setRouteId(trip.getRouteScheduleId());
            order.setDriverId(trip.getDriverId());
            orderRepo.save(order);
        }

        return tripOrders;
    }

    @Override
    @Transactional
    public void removeOrderFromTrip(UUID tripId, UUID orderId) {
        // Verify trip and order exist
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        // Find and delete the TripOrder
        TripOrder tripOrder = tripOrderRepository.findByTripIdAndOrderId(tripId, orderId);
        if (tripOrder == null) {
            throw new NotFoundException("Order is not assigned to this trip");
        }

        tripOrderRepository.delete(tripOrder);

        // Update order status if needed
        if (order.getStatus() == OrderStatus.DELIVERING) {
            // Check if order is assigned to other trips
            List<TripOrder> otherAssignments = tripOrderRepository.findByOrderId(orderId);
            if (otherAssignments.isEmpty()) {
                // If no other trips, revert to COLLECTED_HUB
                order.setStatus(OrderStatus.COLLECTED_HUB);
                order.setRouteId(null);
                order.setDriverId(null);
                order.setDriverName(null);
                order.setVehicleId(null);
                order.setVehicleType(null);
                order.setVehicleLicensePlate(null);
                orderRepo.save(order);
            }
        }

        log.info("Removed order {} from trip {}", orderId, tripId);
    }

    @Override
    public List<Order> getOrdersForTrip(UUID tripId) {
        // Verify trip exists
        if (!tripRepository.existsById(tripId)) {
            throw new NotFoundException("Trip not found with ID: " + tripId);
        }

        // Get TripOrder entities for this trip
        List<TripOrder> tripOrders = tripOrderRepository.findByTripIdOrderBySequenceNumber(tripId);

        // Get the orders
        List<UUID> orderIds = tripOrders.stream()
                .map(TripOrder::getOrderId)
                .collect(Collectors.toList());

        return orderRepo.findAllById(orderIds);
    }

    @Override
    public List<Trip> getTripsForOrder(UUID orderId) {
        // Verify order exists
        if (!orderRepo.existsById(orderId)) {
            throw new NotFoundException("Order not found with ID: " + orderId);
        }

        // Get TripOrder entities for this order
        List<TripOrder> tripOrders = tripOrderRepository.findByOrderId(orderId);

        // Get the trips
        List<UUID> tripIds = tripOrders.stream()
                .map(TripOrder::getTripId)
                .collect(Collectors.toList());

        return tripRepository.findAllById(tripIds);
    }

    @Override
    @Transactional
    public List<TripOrder> optimizeTripSequence(UUID tripId) {
        // Verify trip exists
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        // Get current orders
        List<Order> orders = getOrdersForTrip(tripId);
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        // Optimize order sequence
        List<Order> optimizedOrders = optimizeOrderSequence(orders, trip);

        // Update TripOrder sequence numbers
        List<TripOrder> tripOrders = tripOrderRepository.findByTripIdOrderBySequenceNumber(tripId);
        Map<UUID, TripOrder> tripOrderMap = tripOrders.stream()
                .collect(Collectors.toMap(TripOrder::getOrderId, to -> to));

        int sequence = 1;
        for (Order order : optimizedOrders) {
            TripOrder tripOrder = tripOrderMap.get(order.getId());
            if (tripOrder != null) {
                tripOrder.setSequenceNumber(sequence++);
                tripOrderRepository.save(tripOrder);
            }
        }

        // Return updated TripOrder entities
        return tripOrderRepository.findByTripIdOrderBySequenceNumber(tripId);
    }
}