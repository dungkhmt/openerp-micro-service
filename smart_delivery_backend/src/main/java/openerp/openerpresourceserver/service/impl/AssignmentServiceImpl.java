package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.VehicleStatus;
import openerp.openerpresourceserver.entity.enumentity.VehicleType;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.AssignmentService;
import openerp.openerpresourceserver.utils.DistanceCalculator.HaversineDistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private HubRepo hubRepo;
    @Autowired
    private SenderRepo senderRepo;
    @Autowired
    private RecipientRepo recipientRepo;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private AssignOrderCollectorRepository assignOrderCollectorRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private  RouteStopRepository routeStopRepository;
    @Autowired
    private  RouteVehicleRepository routeVehicleRepository;
    @Autowired
    private  VehicleRepository vehicleRepository;
    @Autowired
    private OrderItemRepo orderItemRepo;
    @Autowired
    private VehicleLoadRepository vehicleLoadRepository;
    @Autowired
    private VehicleDriverRepository vehicleDriverRepository;
    @Autowired
    private DriverRepo driverRepo;

    private static final double WEIGHT_CAPACITY_FACTOR = 0.6;
    private static final double DISTANCE_FACTOR = 0.3;
    private static final double TIME_FACTOR = 0.1;
    @Override
    public void assignOrderToHub(Order order){
        Sender sender = senderRepo.findById(order.getSenderId()).orElseThrow(() -> new NotFoundException("sender not found"));
        Double x1 = sender.getLatitude();
        Double y1 = sender.getLongitude();
        Recipient recipient = recipientRepo.findById(order.getRecipientId()).orElseThrow(() -> new NotFoundException("sender not found"));
        Double x2 = recipient.getLatitude();
        Double y2 = recipient.getLongitude();
        List<Hub> hubs = hubRepo.findAll();

        Double min1 = Double.MAX_VALUE;
        Double min2 = Double.MAX_VALUE;
        Hub assignedHub1 = null;
        Hub assignedHub2 = null;
        for(Hub hub : hubs){
            // tính khoảng cách kinh độ/vĩ độ trên bản đồ
            Double distance1 = HaversineDistanceCalculator.calculateDistance(x1,y1, hub.getLatitude(), hub.getLongitude() );
            Double distance2 = HaversineDistanceCalculator.calculateDistance(x2,y2, hub.getLatitude(), hub.getLongitude() );

            if (distance1 < min1)
            {
                min1 = distance1;
                assignedHub1 = hub;
            }
            if (distance2 < min2)
            {
                min2 = distance2;
                assignedHub2 = hub;
            }
        }
        if(min1 > 1000)
            throw new NotFoundException("Không có hub nguồn khả dụng trong phạm vi xung quanh!");
        if(min2 > 1000)
            throw new NotFoundException("Không có hub nguồn khả dụng trong phạm vi xung quanh!");
        order.setOriginHubId(assignedHub1.getHubId());
        order.setFinalHubId(assignedHub2.getHubId());
        order.setDistance(min1);

    }

    public void updateAssignmentStatus(UUID assignmentId, CollectorAssignmentStatus status){
        AssignOrderCollector assignment = assignOrderCollectorRepository.findById(assignmentId).orElseThrow(() -> new NotFoundException("not found assignment"));
        assignment.setStatus(status);
        if(status == CollectorAssignmentStatus.COMPLETED){
            Order order = orderRepo.findById(assignment.getOrderId()).orElseThrow(()-> new NotFoundException("order not found"));
            order.setStatus(OrderStatus.COLLECTED_COLLECTOR);
        }
        assignOrderCollectorRepository.save(assignment);
    }


    @Override
    @Transactional
    public boolean assignOrderToRoute(UUID orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        // Check if order is in a valid state for auto-assignment
//        if (order.getStatus() != OrderStatus.COLLECTED_HUB) {
//            log.warn("Order {} is not ready for route assignment. Current status: {}", orderId, order.getStatus());
//            return false;
//        }

        // Calculate order metrics (weight, volume, etc.)
        OrderMetrics orderMetrics = calculateOrderMetrics(order);

        // Find optimal route-vehicle combination for this order
        Optional<RouteVehicleAssignment> optimalAssignment = findOptimalRouteVehicleAssignment(order, orderMetrics);

        if (optimalAssignment.isEmpty()) {
            log.warn("No suitable route-vehicle combination found for order {}", orderId);
            return false;
        }

        RouteVehicleAssignment assignment = optimalAssignment.get();

        // Assign the order to the selected route and vehicle
        order.setRouteId(assignment.getRouteId());
        order.setVehicleId(assignment.getVehicleId());
        order.setVehicleType(assignment.getVehicleType());
        order.setVehicleLicensePlate(assignment.getVehiclePlateNumber());
        order.setDriverId(assignment.getDriverId());
        order.setDriverName(assignment.getDriverName());
        order.setStatus(OrderStatus.DELIVERING);

        // Update vehicle current capacity if needed
        updateVehicleStatus(assignment.getVehicleId(), orderMetrics);

        // Save the updated order
        orderRepo.save(order);

        log.info("Order {} assigned to route {} with vehicle {}",
                orderId, assignment.getRouteId(), assignment.getVehicleId());

        return true;
    }

    @Override
    @Transactional
    public int assignMultipleOrdersToRoutes(List<UUID> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return 0;
        }

        // Get all orders that need assignment
        List<Order> validOrders = orderRepo.findAllById(orderIds).stream()
                .filter(order -> order.getStatus() == OrderStatus.COLLECTED_HUB)
                .collect(Collectors.toList());

        if (validOrders.isEmpty()) {
            return 0;
        }

        // Calculate metrics for all orders
        Map<UUID, OrderMetrics> orderMetricsMap = new HashMap<>();
        for (Order order : validOrders) {
            orderMetricsMap.put(order.getId(), calculateOrderMetrics(order));
        }

        // Get all available routes and their stops
        List<Route> activeRoutes = routeRepository.findByStatus(Route.RouteStatus.ACTIVE);
        Map<UUID, List<RouteStop>> routeStopsMap = new HashMap<>();
        for (Route route : activeRoutes) {
            routeStopsMap.put(route.getRouteId(),
                    routeStopRepository.findByRouteIdOrderByStopSequence(route.getRouteId()));
        }

        // Get all available vehicles
        List<Vehicle> availableVehicles = vehicleRepository.findAll().stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE || v.getStatus() == VehicleStatus.ASSIGNED)
                .collect(Collectors.toList());

        // Group orders by route suitability
        Map<Route, List<Order>> routeOrdersMap = groupOrdersByRoute(validOrders, activeRoutes, routeStopsMap);

        // For each route, assign orders to the most suitable vehicles
        int assignedCount = 0;
        for (Map.Entry<Route, List<Order>> entry : routeOrdersMap.entrySet()) {
            Route route = entry.getKey();
            List<Order> routeOrders = entry.getValue();

            // Sort orders by priority (could be based on creation time, urgency, etc.)
            routeOrders.sort(Comparator.comparing(Order::getCreatedAt));

            // For each order, find the best vehicle
            for (Order order : routeOrders) {
                // Get suitable vehicles for this route
                List<Vehicle> routeVehicles = getVehiclesForRoute(route.getRouteId(), availableVehicles);

                if (routeVehicles.isEmpty()) {
                    continue;
                }

                // Find the best vehicle for this order
                Optional<Vehicle> bestVehicle = findBestVehicleForOrder(
                        order,
                        orderMetricsMap.get(order.getId()),
                        routeVehicles
                );

                if (bestVehicle.isPresent()) {
                    Vehicle vehicle = bestVehicle.get();

                    // Assign order to route and vehicle
                    order.setRouteId(route.getRouteId());
                    order.setVehicleId(vehicle.getVehicleId());
                    order.setVehicleType(vehicle.getVehicleType());
                    order.setVehicleLicensePlate(vehicle.getPlateNumber());


                    // Update vehicle status
                    updateVehicleStatus(vehicle.getVehicleId(), orderMetricsMap.get(order.getId()));

                    // Save changes
                    orderRepo.save(order);
                    assignedCount++;
                }
            }
        }

        return assignedCount;
    }

    @Override
    @Transactional
    public int assignPendingOrdersToRoutes(UUID hubId) {
        // Find all orders at this hub that are ready for delivery
        List<Order> pendingOrders = orderRepo.findAll().stream()
                .filter(order -> order.getOriginHubId() != null &&
                        order.getOriginHubId().equals(hubId) &&
                        order.getStatus() == OrderStatus.COLLECTED_HUB)
                .collect(Collectors.toList());

        List<UUID> pendingOrderIds = pendingOrders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        return assignMultipleOrdersToRoutes(pendingOrderIds);
    }

    /**
     * Calculate metrics for an order (weight, volume, etc.)
     */
    private OrderMetrics calculateOrderMetrics(Order order) {
        // Get order items
        List<OrderItem> items = orderItemRepo.findAllByOrderId(order.getId());

        double totalWeight = 0.0;
        double totalVolume = 0.0;

        for (OrderItem item : items) {
            totalWeight += item.getWeight() * (item.getQuantity() != null ? item.getQuantity() : 1);

            // Calculate volume if dimensions are available
            if (item.getLength() != null && item.getWidth() != null && item.getHeight() != null) {
                double itemVolume = item.getLength() * item.getWidth() * item.getHeight();
                totalVolume += itemVolume * (item.getQuantity() != null ? item.getQuantity() : 1);
            }
        }

        return new OrderMetrics(totalWeight, totalVolume, items.size());
    }

    /**
     * Find the optimal route and vehicle for an order
     */
    private Optional<RouteVehicleAssignment> findOptimalRouteVehicleAssignment(Order order, OrderMetrics metrics) {
        if (order.getOriginHubId() == null || order.getFinalHubId() == null) {
            log.warn("Order {} is missing origin or destination hub", order.getId());
            return Optional.empty();
        }

        // Find suitable routes that connect the origin and destination hubs
        List<Route> suitableRoutes = findSuitableRoutes(order.getOriginHubId(), order.getFinalHubId());
        if (suitableRoutes.isEmpty()) {
            log.warn("No suitable routes found for order {} between hubs {} and {}",
                    order.getId(), order.getOriginHubId(), order.getFinalHubId());
            return Optional.empty();
        }

        // Get all available vehicles
        List<Vehicle> availableVehicles = vehicleRepository.findAll().stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE || v.getStatus() == VehicleStatus.ASSIGNED)
                .collect(Collectors.toList());

        if (availableVehicles.isEmpty()) {
            return Optional.empty();
        }

        RouteVehicleAssignment bestAssignment = null;
        double bestScore = Double.MIN_VALUE;

        for (Route route : suitableRoutes) {
            List<Vehicle> routeVehicles = getVehiclesForRoute(route.getRouteId(), availableVehicles);

            for (Vehicle vehicle : routeVehicles) {
                // Check if vehicle can handle the order
                if (!canVehicleHandleOrder(vehicle, metrics)) {
                    continue;
                }

                // Calculate score for this route-vehicle combination
                double score = calculateAssignmentScore(order, route, vehicle, metrics);

                if (score > bestScore) {
                    bestScore = score;
                    VehicleDriver vehicleDriver = vehicleDriverRepository.findByVehicleId(vehicle.getVehicleId());
                    if(vehicleDriver == null) throw  new NotFoundException("not found vehicle driver with vehicle id: " + vehicle.getVehicleId() + " in route vehicle assignment service");
                    Driver driver = driverRepo.findById(vehicleDriver.getDriverId()).orElseThrow(() -> new NotFoundException("not found driver with id: " + vehicleDriver.getDriverId() + " in route vehicle assignment service"));

                    bestAssignment = new RouteVehicleAssignment(
                            route.getRouteId(),
                            vehicle.getVehicleId(),
                            vehicle.getVehicleType(),
                            vehicle.getPlateNumber(),
                            driver.getId(),
                            driver.getName(),
                            score
                    );
                }
            }
        }

        return Optional.ofNullable(bestAssignment);
    }

    /**
     * Group orders by suitable routes
     */
    private Map<Route, List<Order>> groupOrdersByRoute(
            List<Order> orders,
            List<Route> routes,
            Map<UUID, List<RouteStop>> routeStopsMap) {

        Map<Route, List<Order>> result = new HashMap<>();

        for (Order order : orders) {
            if (order.getOriginHubId() == null || order.getFinalHubId() == null) {
                continue;
            }

            for (Route route : routes) {
                List<RouteStop> stops = routeStopsMap.get(route.getRouteId());

                if (isRouteSuitableForOrder(stops, order.getOriginHubId(), order.getFinalHubId())) {
                    result.computeIfAbsent(route, k -> new ArrayList<>()).add(order);
                }
            }
        }

        return result;
    }

    /**
     * Check if a route is suitable for an order (connects origin and destination in correct sequence)
     */
    private boolean isRouteSuitableForOrder(List<RouteStop> stops, UUID originHubId, UUID destinationHubId) {
        // Create a map of hub IDs to their sequence in the route
        Map<UUID, Integer> hubSequence = new HashMap<>();
        for (RouteStop stop : stops) {
            hubSequence.put(stop.getHubId(), stop.getStopSequence());
        }

        // Check if both origin and destination hubs are on this route
        if (hubSequence.containsKey(originHubId) && hubSequence.containsKey(destinationHubId)) {
            // Make sure the origin comes before the destination in the route sequence
            return hubSequence.get(originHubId) < hubSequence.get(destinationHubId);
        }

        return false;
    }

    /**
     * Get vehicles assigned to a specific route
     */
    private List<Vehicle> getVehiclesForRoute(UUID routeId, List<Vehicle> availableVehicles) {
        List<RouteVehicle> routeVehicles = routeVehicleRepository.findByRouteId(routeId);
        Set<UUID> vehicleIds = routeVehicles.stream()
                .map(RouteVehicle::getVehicleId)
                .collect(Collectors.toSet());

        return availableVehicles.stream()
                .filter(v -> vehicleIds.contains(v.getVehicleId()))
                .collect(Collectors.toList());
    }

    /**
     * Check if a vehicle can handle an order (capacity, weight, etc.)
     */
    private boolean canVehicleHandleOrder(Vehicle vehicle, OrderMetrics metrics) {
        // If vehicle has no capacity or metrics has no weight/volume, assume it's compatible
        if (vehicle.getWeightCapacity() == null || metrics.getTotalWeight() == 0) {
            return true;
        }

        // Check weight capacity
        double currentWeightLoad = calculateCurrentVehicleWeightLoad(vehicle.getVehicleId());
        double remainingWeightCapacity = vehicle.getWeightCapacity() - currentWeightLoad;

        // Check volume capacity if applicable
        boolean weightCheck = remainingWeightCapacity >= metrics.getTotalWeight();

        boolean volumeCheck = true;
        if (vehicle.getVolumeCapacity() != null && metrics.getTotalVolume() > 0) {
            double currentVolumeLoad = calculateCurrentVehicleVolumeLoad(vehicle.getVehicleId());
            double remainingVolumeCapacity = vehicle.getVolumeCapacity() - currentVolumeLoad;
            volumeCheck = remainingVolumeCapacity >= metrics.getTotalVolume();
        }

        return weightCheck && volumeCheck;
    }


    /**
     * Calculate the current weight load on a vehicle
     */
    private double calculateCurrentVehicleWeightLoad(UUID vehicleId) {
        Optional<VehicleLoad> vehicleLoadOpt = vehicleLoadRepository.findByVehicleId(vehicleId);

        if (vehicleLoadOpt.isPresent()) {
            return vehicleLoadOpt.get().getCurrentWeightLoad();
        }

        // If no tracked load exists yet, calculate from assigned orders
        List<Order> assignedOrders = orderRepo.findAll().stream()
                .filter(order -> vehicleId.equals(order.getVehicleId()) &&
                        order.getStatus() == OrderStatus.DELIVERING)
                .collect(Collectors.toList());

        double totalWeight = 0.0;

        for (Order order : assignedOrders) {
            List<OrderItem> items = orderItemRepo.findAllByOrderId(order.getId());
            for (OrderItem item : items) {
                int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
                totalWeight += item.getWeight() * quantity;
            }
        }

        // Create and save new vehicle load record
        VehicleLoad vehicleLoad = new VehicleLoad(vehicleId);
        vehicleLoad.setCurrentWeightLoad(totalWeight);
        vehicleLoadRepository.save(vehicleLoad);

        return totalWeight;
    }

    /**
     * Calculate the current volume load on a vehicle
     */
    private double calculateCurrentVehicleVolumeLoad(UUID vehicleId) {
        Optional<VehicleLoad> vehicleLoadOpt = vehicleLoadRepository.findByVehicleId(vehicleId);

        if (vehicleLoadOpt.isPresent()) {
            return vehicleLoadOpt.get().getCurrentVolumeLoad();
        }

        // If no tracked load exists yet, calculate from assigned orders
        List<Order> assignedOrders = orderRepo.findAll().stream()
                .filter(order -> vehicleId.equals(order.getVehicleId()) &&
                        order.getStatus() == OrderStatus.DELIVERING)
                .collect(Collectors.toList());

        double totalVolume = 0.0;

        for (Order order : assignedOrders) {
            List<OrderItem> items = orderItemRepo.findAllByOrderId(order.getId());
            for (OrderItem item : items) {
                int quantity = item.getQuantity() != null ? item.getQuantity() : 1;

                // Calculate volume if dimensions are available
                if (item.getLength() != null && item.getWidth() != null && item.getHeight() != null) {
                    double itemVolume = item.getLength() * item.getWidth() * item.getHeight();
                    totalVolume += itemVolume * quantity;
                }
            }
        }

        // Create and save new vehicle load record
        VehicleLoad vehicleLoad = new VehicleLoad(vehicleId);
        vehicleLoad.setCurrentVolumeLoad(totalVolume);
        vehicleLoadRepository.save(vehicleLoad);

        return totalVolume;
    }
    /**
     * Find the best vehicle for an order
     */
    private Optional<Vehicle> findBestVehicleForOrder(Order order, OrderMetrics metrics, List<Vehicle> vehicles) {
        Vehicle bestVehicle = null;
        double bestScore = Double.MIN_VALUE;

        for (Vehicle vehicle : vehicles) {
            if (!canVehicleHandleOrder(vehicle, metrics)) {
                continue;
            }

            // Calculate score for vehicle suitability
            double score = calculateVehicleScore(vehicle, metrics);

            if (score > bestScore) {
                bestScore = score;
                bestVehicle = vehicle;
            }
        }

        return Optional.ofNullable(bestVehicle);
    }

    /**
     * Calculate score for a vehicle's suitability for an order
     */
    /**
     * Calculate score for a vehicle's suitability for an order
     */
    private double calculateVehicleScore(Vehicle vehicle, OrderMetrics metrics) {
        double score = 0.0;

        // Weight capacity utilization score
        if (vehicle.getWeightCapacity() != null && metrics.getTotalWeight() > 0) {
            double currentLoad = calculateCurrentVehicleWeightLoad(vehicle.getVehicleId());
            double remainingCapacity = vehicle.getWeightCapacity() - currentLoad;

            // Calculate what the new utilization would be after adding this order
            double newTotalLoad = currentLoad + metrics.getTotalWeight();
            double newUtilizationRatio = newTotalLoad / vehicle.getWeightCapacity();

            // Ideal utilization is around 70-80%
            if (newUtilizationRatio <= 0.8) {
                score += (newUtilizationRatio / 0.8) * 50; // Score up to 50 points for good utilization
            } else {
                // Penalty for overloading or almost overloading
                score -= (newUtilizationRatio - 0.8) * 100;
            }

            // Additional score for how well this order fits the remaining space
            double fitRatio = metrics.getTotalWeight() / remainingCapacity;
            if (fitRatio <= 1.0) {
                score += fitRatio * 20; // Up to 20 additional points for good fit
            }
        } else {
            // Default score if capacity or weight not available
            score += 30;
        }

        // Volume capacity utilization score (if applicable)
        if (vehicle.getVolumeCapacity() != null && metrics.getTotalVolume() > 0) {
            double currentVolumeLoad = calculateCurrentVehicleVolumeLoad(vehicle.getVehicleId());
            double remainingVolumeCapacity = vehicle.getVolumeCapacity() - currentVolumeLoad;

            // Calculate volume utilization
            double newTotalVolumeLoad = currentVolumeLoad + metrics.getTotalVolume();
            double newVolumeUtilizationRatio = newTotalVolumeLoad / vehicle.getVolumeCapacity();

            // Ideal utilization is around 70-80%
            if (newVolumeUtilizationRatio <= 0.8) {
                score += (newVolumeUtilizationRatio / 0.8) * 30; // Score up to 30 points for good volume utilization
            } else {
                // Penalty for overloading or almost overloading
                score -= (newVolumeUtilizationRatio - 0.8) * 60;
            }
        }

        // Vehicle type suitability
        if (metrics.getTotalWeight() > 1000 && vehicle.getVehicleType() == VehicleType.TRUCK) {
            score += 20; // Trucks better for heavy loads
        } else if (metrics.getTotalWeight() <= 1000 && vehicle.getVehicleType() == VehicleType.CAR) {
            score += 20; // Cars better for lighter loads
        }

        return score;
    }


    /**
     * Calculate a score for a route-vehicle assignment
     */
    private double calculateAssignmentScore(Order order, Route route, Vehicle vehicle, OrderMetrics metrics) {
        double score = 0.0;

        // Weight/capacity optimization score
        double capacityScore = 0.0;
        if (vehicle.getWeightCapacity() != null && metrics.getTotalWeight() > 0) {
            double currentLoad = calculateCurrentVehicleWeightLoad(vehicle.getVehicleId());
            double newTotalLoad = currentLoad + metrics.getTotalWeight();
            double newUtilizationRatio = newTotalLoad / vehicle.getWeightCapacity();

            // Optimal utilization is around 70-80%
            if (newUtilizationRatio <= 0.8) {
                capacityScore = (newUtilizationRatio / 0.8) * 100; // Score up to 100 points
            } else {
                // Penalty for overloading or almost overloading
                capacityScore = Math.max(0, 100 - ((newUtilizationRatio - 0.8) * 500));
            }

            // Add bonus for vehicles that are already in use but not full
            // (to promote consolidation of orders on fewer vehicles)
            if (currentLoad > 0 && newUtilizationRatio <= 0.9) {
                capacityScore += 25; // Bonus for consolidating orders
            }
        } else {
            capacityScore = 50; // Default score
        }

        // Distance optimization score
        double distanceScore = 0.0;
        if (route.getTotalDistance() != null) {
            // Shorter routes get better scores
            distanceScore = Math.max(0, 100 - (route.getTotalDistance() / 10)); // Assume 0-1000km scale
        } else {
            distanceScore = 50; // Default score
        }

        // Time optimization score
        double timeScore = 0.0;
        if (route.getEstimatedDuration() != null) {
            // Shorter durations get better scores
            timeScore = Math.max(0, 100 - (route.getEstimatedDuration() / 6)); // Assume 0-600 minutes scale
        } else {
            timeScore = 50; // Default score
        }

        // Combine scores with weights
        score = (capacityScore * WEIGHT_CAPACITY_FACTOR) +
                (distanceScore * DISTANCE_FACTOR) +
                (timeScore * TIME_FACTOR);

        return score;
    }

    /**
     * Update a vehicle's status and load after assigning an order
     */
    private void updateVehicleStatus(UUID vehicleId, OrderMetrics metrics) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found: " + vehicleId));

//        // Update status to ASSIGNED if it was AVAILABLE
//        if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {
//            vehicle.setStatus(VehicleStatus.ASSIGNED);
//            vehicleRepository.save(vehicle);
//        }

        // Update vehicle load
        VehicleLoad vehicleLoad = vehicleLoadRepository.findByVehicleId(vehicleId)
                .orElse(new VehicleLoad(vehicleId));

        // Add the new order's metrics to the current load
        vehicleLoad.setCurrentWeightLoad(vehicleLoad.getCurrentWeightLoad() + metrics.getTotalWeight());

        if (metrics.getTotalVolume() > 0) {
            double currentVolume = vehicleLoad.getCurrentVolumeLoad() != null ?
                    vehicleLoad.getCurrentVolumeLoad() : 0.0;
            vehicleLoad.setCurrentVolumeLoad(currentVolume + metrics.getTotalVolume());
        }

        int currentItems = vehicleLoad.getCurrentItemCount() != null ?
                vehicleLoad.getCurrentItemCount() : 0;
        vehicleLoad.setCurrentItemCount(currentItems + metrics.getItemCount());

        // Save updated load
        vehicleLoadRepository.save(vehicleLoad);

        log.info("Updated vehicle {} load: weight={}, volume={}, items={}",
                vehicleId,
                vehicleLoad.getCurrentWeightLoad(),
                vehicleLoad.getCurrentVolumeLoad(),
                vehicleLoad.getCurrentItemCount());
    }

    /**
     * Find routes that connect origin and destination hubs
     */
    /**
     * Decrease a vehicle's load after an order is completed or removed
     */
    @Override
    @Transactional
    public void decreaseVehicleLoad(UUID vehicleId, UUID orderId) {
        // Get order metrics
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        OrderMetrics metrics = calculateOrderMetrics(order);

        // Update vehicle load
        VehicleLoad vehicleLoad = vehicleLoadRepository.findByVehicleId(vehicleId)
                .orElse(new VehicleLoad(vehicleId));

        // Subtract the order's metrics from the current load
        double newWeight = Math.max(0, vehicleLoad.getCurrentWeightLoad() - metrics.getTotalWeight());
        vehicleLoad.setCurrentWeightLoad(newWeight);

        if (metrics.getTotalVolume() > 0 && vehicleLoad.getCurrentVolumeLoad() != null) {
            double newVolume = Math.max(0, vehicleLoad.getCurrentVolumeLoad() - metrics.getTotalVolume());
            vehicleLoad.setCurrentVolumeLoad(newVolume);
        }

        if (vehicleLoad.getCurrentItemCount() != null) {
            int newItems = Math.max(0, vehicleLoad.getCurrentItemCount() - metrics.getItemCount());
            vehicleLoad.setCurrentItemCount(newItems);
        }

        // Save updated load
        vehicleLoadRepository.save(vehicleLoad);

        // If vehicle is now empty, update status to AVAILABLE
        if (newWeight == 0) {
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new NotFoundException("Vehicle not found: " + vehicleId));

            if (vehicle.getStatus() == VehicleStatus.ASSIGNED) {
                vehicle.setStatus(VehicleStatus.AVAILABLE);
                vehicleRepository.save(vehicle);
            }
        }

        log.info("Decreased vehicle {} load after order {}: weight={}, volume={}, items={}",
                vehicleId, orderId,
                vehicleLoad.getCurrentWeightLoad(),
                vehicleLoad.getCurrentVolumeLoad(),
                vehicleLoad.getCurrentItemCount());
    }

    private List<Route> findSuitableRoutes(UUID originHubId, UUID destinationHubId) {
        List<Route> allRoutes = routeRepository.findByStatus(Route.RouteStatus.ACTIVE);
        List<Route> suitableRoutes = new ArrayList<>();

        for (Route route : allRoutes) {
            List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopSequence(route.getRouteId());

            if (isRouteSuitableForOrder(stops, originHubId, destinationHubId)) {
                suitableRoutes.add(route);
            }
        }

        return suitableRoutes;
    }

    /**
     * Class to hold order metrics
     */
    private static class OrderMetrics {
        private final double totalWeight;
        private final double totalVolume;
        private final int itemCount;

        public OrderMetrics(double totalWeight, double totalVolume, int itemCount) {
            this.totalWeight = totalWeight;
            this.totalVolume = totalVolume;
            this.itemCount = itemCount;
        }

        public double getTotalWeight() {
            return totalWeight;
        }

        public double getTotalVolume() {
            return totalVolume;
        }

        public int getItemCount() {
            return itemCount;
        }
    }

    /**
     * Class to hold route-vehicle assignment details
     */
    private static class RouteVehicleAssignment {
        private final UUID routeId;
        private final UUID vehicleId;
        private final VehicleType vehicleType;
        private final String vehiclePlateNumber;
        private final UUID driverId;
        private final String driverName;
        private final double score;

        public RouteVehicleAssignment(
                UUID routeId,
                UUID vehicleId,
                VehicleType vehicleType,
                String vehiclePlateNumber,
                UUID driverId,
                String driverName,
                double score) {
            this.routeId = routeId;
            this.vehicleId = vehicleId;
            this.vehicleType = vehicleType;
            this.vehiclePlateNumber = vehiclePlateNumber;
            this.driverId = driverId;
            this.driverName = driverName;
            this.score = score;
        }

        public UUID getRouteId() {
            return routeId;
        }

        public UUID getVehicleId() {
            return vehicleId;
        }

        public VehicleType getVehicleType() {
            return vehicleType;
        }

        public String getVehiclePlateNumber() {
            return vehiclePlateNumber;
        }

        public UUID getDriverId() {
            return driverId;
        }

        public String getDriverName() {
            return driverName;
        }

        public double getScore() {
            return score;
        }
    }
}
