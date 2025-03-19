package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.Setter;
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

/**
 * Implementation of the AssignmentService interface responsible for:
 * - Assigning orders to appropriate hubs
 * - Assigning orders to vehicles for direct delivery
 * - Managing collector assignments
 * - Tracking vehicle load
 */
//<p>This service handles a single-hub direct delivery model where:</p>
//        * <ol>
// *   <li>Orders are collected at origin hubs and prepared for delivery</li>
//        *   <li>Vehicles are assigned to pick up orders from a single hub</li>
//        *   <li>Each vehicle can be loaded with multiple orders from that hub</li>
//        *   <li>Vehicles deliver directly to recipients without additional pickups</li>
//        * </ol>
//        *
//        * <p><b>Assignment Algorithm:</b></p>
//        * <p>The service uses a greedy algorithm to optimize vehicle assignments:</p>
//        *
//        * <ol>
// *   <li><b>Find suitable vehicles:</b> For each order, identify vehicles that can physically handle it</li>
//        *   <li><b>Prioritize difficult assignments:</b> Process orders with fewer vehicle options first</li>
//        *   <li><b>Score vehicles:</b> Rank vehicles based on:
//        *     <ul>
// *       <li>Capacity utilization (70% weight) - optimal is around 75% capacity</li>
//        *       <li>Vehicle type suitability (30% weight) - matching vehicle types to order characteristics</li>
//        *     </ul>
//        *   </li>
//        *   <li><b>Optimize consolidation:</b> Prefer partially loaded vehicles when possible to reduce the total number of vehicles used</li>
//        *   <li><b>Respect constraints:</b> Never exceed vehicle capacity limits (maintains 90% maximum safe utilization)</li>
//        * </ol>
//        *
//        * <p><b>Features:</b></p>
//        * <ul>
// *   <li>Efficient batch processing of multiple orders</li>
//        *   <li>In-memory tracking of vehicle capacity during batch assignments</li>
//        *   <li>Consideration of both weight and volume constraints</li>
//        *   <li>Vehicle load balancing to maximize fleet utilization</li>
//        *   <li>Vehicle type matching based on order characteristics</li>
//        * </ul>
//        *
//        * <p>This implementation focuses on maximizing delivery efficiency by optimizing vehicle utilization
// * while ensuring timely delivery and respecting all physical constraints.</p>
//        */
@Log4j2
@Service
public class AssignmentServiceImpl implements AssignmentService {

    // Repository dependencies
    @Autowired private HubRepo hubRepo;
    @Autowired private SenderRepo senderRepo;
    @Autowired private RecipientRepo recipientRepo;
    @Autowired private OrderRepo orderRepo;
    @Autowired private AssignOrderCollectorRepository assignOrderCollectorRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private OrderItemRepo orderItemRepo;
    @Autowired private VehicleLoadRepository vehicleLoadRepository;
    @Autowired private VehicleDriverRepository vehicleDriverRepository;
    @Autowired private DriverRepo driverRepo;

    // Weighting factors for the vehicle selection algorithm
    private static final double CAPACITY_UTILIZATION_FACTOR = 0.7;  // Higher priority for capacity utilization
    private static final double VEHICLE_TYPE_FACTOR = 0.3;          // Lower priority for vehicle type suitability

    // Optimal vehicle utilization target (percentage)
    private static final double OPTIMAL_UTILIZATION = 0.75;
    private static final double MAX_SAFE_UTILIZATION = 0.9;

    /**
     * Assigns an order to the closest hubs for origin and destination.
     *
     * @param order The order to be assigned to hubs
     * @throws NotFoundException if no suitable hubs are found within range
     */
    @Override
    public void assignOrderToHub(Order order) {
        Sender sender = senderRepo.findById(order.getSenderId())
                .orElseThrow(() -> new NotFoundException("sender not found"));
        Double x1 = sender.getLatitude();
        Double y1 = sender.getLongitude();

        Recipient recipient = recipientRepo.findById(order.getRecipientId())
                .orElseThrow(() -> new NotFoundException("recipient not found"));
        Double x2 = recipient.getLatitude();
        Double y2 = recipient.getLongitude();

        List<Hub> hubs = hubRepo.findAll();

        Double min1 = Double.MAX_VALUE;
        Double min2 = Double.MAX_VALUE;
        Hub assignedHub1 = null;
        Hub assignedHub2 = null;

        for (Hub hub : hubs) {
            // Calculate distances using Haversine formula (accounts for Earth's curvature)
            Double distance1 = HaversineDistanceCalculator.calculateDistance(x1, y1, hub.getLatitude(), hub.getLongitude());
            Double distance2 = HaversineDistanceCalculator.calculateDistance(x2, y2, hub.getLatitude(), hub.getLongitude());

            if (distance1 < min1) {
                min1 = distance1;
                assignedHub1 = hub;
            }
            if (distance2 < min2) {
                min2 = distance2;
                assignedHub2 = hub;
            }
        }

        // Validate that suitable hubs were found within range (1000 km)
        if (min1 > 1000)
            throw new NotFoundException("No available origin hub within range!");
        if (min2 > 1000)
            throw new NotFoundException("No available destination hub within range!");

        order.setOriginHubId(assignedHub1.getHubId());
        order.setFinalHubId(assignedHub2.getHubId());
        order.setDistance(min1);
    }

    /**
     * Updates the status of a collector assignment and the related order if necessary.
     *
     * @param assignmentId The ID of the assignment to update
     * @param status The new status to set
     * @throws NotFoundException if the assignment is not found
     */
    public void updateAssignmentStatus(UUID assignmentId, CollectorAssignmentStatus status) {
        AssignOrderCollector assignment = assignOrderCollectorRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));

        assignment.setStatus(status);

        // If assignment is completed, update the order status accordingly
        if (status == CollectorAssignmentStatus.COMPLETED) {
            Order order = orderRepo.findById(assignment.getOrderId())
                    .orElseThrow(() -> new NotFoundException("Order not found"));
            order.setStatus(OrderStatus.COLLECTED_COLLECTOR);
        }

        assignOrderCollectorRepository.save(assignment);
    }

    /**
     * Assigns a single order to a vehicle for direct delivery.
     * Uses a greedy algorithm to select the best vehicle based on:
     * 1. Vehicle capacity utilization
     * 2. Vehicle type suitability
     *
     * @param orderId The ID of the order to assign
     * @return true if assignment was successful, false otherwise
     */
    @Override
    @Transactional
    public boolean assignOrderToRoute(UUID orderId) {
        log.info("Attempting to assign order {} to a vehicle for direct delivery", orderId);

        // Retrieve and validate the order
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        // Verify order is in the correct state for delivery assignment
        if (order.getStatus() != OrderStatus.PENDING ) {
            log.warn("Order {} is not ready for delivery assignment. Current status: {}",
                    orderId, order.getStatus());
            return false;
        }

        // Calculate order metrics needed for assignment decisions
        OrderMetrics orderMetrics = calculateOrderMetrics(order);
        log.debug("Order {} metrics - Weight: {}, Volume: {}, Items: {}",
                orderId, orderMetrics.getTotalWeight(),
                orderMetrics.getTotalVolume(), orderMetrics.getItemCount());

        // Find all available vehicles
        List<Vehicle> availableVehicles = findAvailableVehicles();
        if (availableVehicles.isEmpty()) {
            log.warn("No available vehicles found for order {}", orderId);
            return false;
        }

        // Find vehicles that can handle this order
        List<VehicleCandidate> suitableVehicles = findSuitableVehicles(availableVehicles, orderMetrics);
        if (suitableVehicles.isEmpty()) {
            log.warn("No suitable vehicles found for order {}", orderId);
            return false;
        }

        // Select the best vehicle using our scoring algorithm
        Optional<VehicleCandidate> bestVehicle = selectBestVehicle(suitableVehicles, orderMetrics);
        if (!bestVehicle.isPresent()) {
            log.warn("Could not select best vehicle for order {}", orderId);
            return false;
        }

        // Assign the order to the selected vehicle
        VehicleCandidate selected = bestVehicle.get();
        assignOrderToVehicle(order, selected);

        // Update vehicle load
        updateVehicleLoad(selected.getVehicleId(), orderMetrics);

        log.info("Successfully assigned order {} to vehicle {}",
                orderId, selected.getVehicleId());

        return true;
    }

    /**
     * Assigns multiple orders to vehicles for direct delivery.
     * Uses a greedy algorithm that prioritizes orders with fewer vehicle options.
     *
     * @param orderIds List of order IDs to assign
     * @return Number of orders successfully assigned
     */
    @Override
    @Transactional
    public int assignMultipleOrdersToRoutes(List<UUID> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return 0;
        }

        log.info("Attempting to assign {} orders to vehicles for direct delivery", orderIds.size());

        // Get all orders in COLLECTED_HUB status
        List<Order> validOrders = orderRepo.findAllById(orderIds).stream()
                .filter(order -> order.getStatus() == OrderStatus.COLLECTED_HUB)
                .collect(Collectors.toList());

        if (validOrders.isEmpty()) {
            log.info("No valid orders found for assignment");
            return 0;
        }

        // Calculate metrics for all orders
        Map<UUID, OrderMetrics> orderMetricsMap = new HashMap<>();
        for (Order order : validOrders) {
            orderMetricsMap.put(order.getId(), calculateOrderMetrics(order));
        }

        // Find all available vehicles
        List<Vehicle> availableVehicles = findAvailableVehicles();
        if (availableVehicles.isEmpty()) {
            log.warn("No available vehicles found for any orders");
            return 0;
        }

        // Find suitable vehicles for each order
        Map<UUID, List<VehicleCandidate>> orderVehiclesMap = new HashMap<>();
        for (Order order : validOrders) {
            UUID orderId = order.getId();
            OrderMetrics metrics = orderMetricsMap.get(orderId);
            List<VehicleCandidate> suitableVehicles = findSuitableVehicles(availableVehicles, metrics);

            if (!suitableVehicles.isEmpty()) {
                orderVehiclesMap.put(orderId, suitableVehicles);
                log.debug("Found {} suitable vehicles for order {}",
                        suitableVehicles.size(), orderId);
            } else {
                log.warn("No suitable vehicles found for order {}", orderId);
            }
        }

        if (orderVehiclesMap.isEmpty()) {
            log.warn("No orders have suitable vehicles");
            return 0;
        }

        // Sort orders by assignment difficulty (fewer options first)
        List<UUID> prioritizedOrderIds = orderVehiclesMap.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue().size()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Track vehicle remaining capacity as we make assignments
        Map<UUID, VehicleCapacity> vehicleCapacityMap = new HashMap<>();
        Set<UUID> assignedOrders = new HashSet<>();

        // Process each order in priority order
        for (UUID orderId : prioritizedOrderIds) {
            Order order = validOrders.stream()
                    .filter(o -> o.getId().equals(orderId))
                    .findFirst().orElse(null);

            if (order == null) continue;

            OrderMetrics metrics = orderMetricsMap.get(orderId);
            List<VehicleCandidate> candidates = orderVehiclesMap.get(orderId);

            // Filter candidates by current vehicle capacity
            candidates = filterCandidatesByRemainingCapacity(candidates, metrics, vehicleCapacityMap);
            if (candidates.isEmpty()) {
                log.warn("No vehicles with sufficient remaining capacity for order {}", orderId);
                continue;
            }

            // Select best vehicle for this order
            Optional<VehicleCandidate> bestVehicle = selectBestVehicle(candidates, metrics);
            if (!bestVehicle.isPresent()) {
                log.warn("Could not select best vehicle for order {}", orderId);
                continue;
            }

            // Perform the assignment
            VehicleCandidate selected = bestVehicle.get();
            assignOrderToVehicle(order, selected);

            // Update vehicle load in database
            updateVehicleLoad(selected.getVehicleId(), metrics);

            // Update our capacity tracking
            updateRemainingCapacity(vehicleCapacityMap, selected.getVehicleId(),
                    selected.getWeightCapacity(), selected.getVolumeCapacity(), metrics);

            // Track assignment
            assignedOrders.add(order.getId());

            log.info("Assigned order {} to vehicle {}",
                    orderId, selected.getVehicleId());
        }

        log.info("Successfully assigned {} orders to vehicles", assignedOrders.size());
        return assignedOrders.size();
    }

    /**
     * Assigns all pending orders from a specific hub to vehicles.
     *
     * @param hubId The ID of the hub containing orders to assign
     * @return Number of orders successfully assigned
     */
    @Override
    @Transactional
    public int assignPendingOrdersToRoutes(UUID hubId) {
        log.info("Assigning all pending orders from hub {}", hubId);

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
     * Decreases a vehicle's load after an order is completed or removed.
     *
     * @param vehicleId The ID of the vehicle
     * @param orderId The ID of the order to remove from the vehicle's load
     */
    @Override
    @Transactional
    public void decreaseVehicleLoad(UUID vehicleId, UUID orderId) {
        log.info("Decreasing vehicle {} load for completed order {}", vehicleId, orderId);

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

        log.info("Updated vehicle {} load after removing order {}: weight={}, volume={}, items={}",
                vehicleId, orderId,
                vehicleLoad.getCurrentWeightLoad(),
                vehicleLoad.getCurrentVolumeLoad(),
                vehicleLoad.getCurrentItemCount());
    }

    // ------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    // ------------------------------------------------------------------------

    /**
     * Finds all vehicles that are available for assignment.
     *
     * @return List of available vehicles
     */
    private List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE ||
                        v.getStatus() == VehicleStatus.ASSIGNED)
                .collect(Collectors.toList());
    }

    /**
     * Finds vehicles that can handle the given order metrics.
     *
     * @param vehicles List of vehicles to check
     * @param metrics Order metrics to evaluate against
     * @return List of suitable vehicle candidates
     */
    private List<VehicleCandidate> findSuitableVehicles(List<Vehicle> vehicles, OrderMetrics metrics) {
        List<VehicleCandidate> suitableVehicles = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            // Skip if vehicle cannot handle this order
            if (!canVehicleHandleOrder(vehicle, metrics)) {
                continue;
            }

            // Find the driver for this vehicle
            VehicleDriver vehicleDriver = vehicleDriverRepository.findByVehicleIdAndUnassignedAtIsNull(vehicle.getVehicleId());
            if (vehicleDriver == null) {
                continue; // Skip if no driver is assigned
            }

            Driver driver = driverRepo.findById(vehicleDriver.getDriverId()).orElse(null);
            if (driver == null) {
                continue; // Skip if driver not found
            }

            // Calculate current load for scoring
            double currentWeightLoad = calculateCurrentVehicleLoad(vehicle.getVehicleId());
            double currentVolumeLoad = 0;
            if (vehicle.getVolumeCapacity() != null) {
                currentVolumeLoad = calculateCurrentVehicleVolumeLoad(vehicle.getVehicleId());
            }

            // Add as a viable candidate
            suitableVehicles.add(new VehicleCandidate(
                    vehicle.getVehicleId(),
                    vehicle.getVehicleType(),
                    vehicle.getPlateNumber(),
                    vehicle.getWeightCapacity().doubleValue(),
                    vehicle.getVolumeCapacity().doubleValue(),
                    currentWeightLoad,
                    currentVolumeLoad,
                    driver.getId(),
                    driver.getName()
            ));
        }

        return suitableVehicles;
    }

    /**
     * Filters vehicle candidates by remaining capacity based on previous assignments.
     *
     * @param candidates The candidates to filter
     * @param metrics The metrics of the order being assigned
     * @param vehicleCapacityMap Map tracking vehicle remaining capacities
     * @return Filtered list of candidates
     */
    private List<VehicleCandidate> filterCandidatesByRemainingCapacity(
            List<VehicleCandidate> candidates,
            OrderMetrics metrics,
            Map<UUID, VehicleCapacity> vehicleCapacityMap) {

        List<VehicleCandidate> filteredCandidates = new ArrayList<>();

        for (VehicleCandidate candidate : candidates) {
            UUID vehicleId = candidate.getVehicleId();

            // If we've already seen this vehicle in previous assignments
            if (vehicleCapacityMap.containsKey(vehicleId)) {
                VehicleCapacity capacity = vehicleCapacityMap.get(vehicleId);

                // Skip if not enough weight capacity remaining
                if (metrics.getTotalWeight() > capacity.getRemainingWeightCapacity()) {
                    continue;
                }

                // Skip if not enough volume capacity remaining (if applicable)
                if (metrics.getTotalVolume() > 0 && capacity.getRemainingVolumeCapacity() != null &&
                        metrics.getTotalVolume() > capacity.getRemainingVolumeCapacity()) {
                    continue;
                }

                // Update current load values for accurate scoring
                candidate.setCurrentWeightLoad(candidate.getWeightCapacity() - capacity.getRemainingWeightCapacity());

                if (capacity.getRemainingVolumeCapacity() != null) {
                    candidate.setCurrentVolumeLoad(candidate.getVolumeCapacity() - capacity.getRemainingVolumeCapacity());
                }
            }
            // Otherwise use the database values which are already set in the candidate

            filteredCandidates.add(candidate);
        }

        return filteredCandidates;
    }

    /**
     * Updates the tracking map for vehicle remaining capacities.
     *
     * @param capacityMap The capacity tracking map
     * @param vehicleId The vehicle ID
     * @param weightCapacity The total weight capacity
     * @param volumeCapacity The total volume capacity
     * @param metrics The metrics of the order just assigned
     */
    private void updateRemainingCapacity(
            Map<UUID, VehicleCapacity> capacityMap,
            UUID vehicleId,
            Double weightCapacity,
            Double volumeCapacity,
            OrderMetrics metrics) {

        if (capacityMap.containsKey(vehicleId)) {
            // Update existing entry
            VehicleCapacity capacity = capacityMap.get(vehicleId);
            capacity.setRemainingWeightCapacity(capacity.getRemainingWeightCapacity() - metrics.getTotalWeight());

            if (capacity.getRemainingVolumeCapacity() != null && metrics.getTotalVolume() > 0) {
                capacity.setRemainingVolumeCapacity(capacity.getRemainingVolumeCapacity() - metrics.getTotalVolume());
            }
        } else {
            // Create new entry based on current database state
            double currentWeightLoad = calculateCurrentVehicleLoad(vehicleId);
            Double currentVolumeLoad = null;
            Double remainingVolumeCapacity = null;

            if (volumeCapacity != null) {
                currentVolumeLoad = calculateCurrentVehicleVolumeLoad(vehicleId);
                remainingVolumeCapacity = volumeCapacity - currentVolumeLoad - metrics.getTotalVolume();
            }

            double remainingWeightCapacity = weightCapacity - currentWeightLoad - metrics.getTotalWeight();

            capacityMap.put(vehicleId, new VehicleCapacity(
                    remainingWeightCapacity,
                    remainingVolumeCapacity
            ));
        }
    }

    /**
     * Selects the best vehicle for an order based on scoring factors.
     *
     * @param candidates The candidates to choose from
     * @param orderMetrics The metrics of the order
     * @return The best vehicle candidate, if one is found
     */
    private Optional<VehicleCandidate> selectBestVehicle(
            List<VehicleCandidate> candidates,
            OrderMetrics orderMetrics) {

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        // Sort candidates by score (highest first)
        candidates.sort((c1, c2) -> {
            double score1 = calculateVehicleScore(c1, orderMetrics);
            double score2 = calculateVehicleScore(c2, orderMetrics);
            return Double.compare(score2, score1);
        });

        // First try to find a vehicle that's already in use but not too full
        for (VehicleCandidate candidate : candidates) {
            double currentLoad = candidate.getCurrentWeightLoad();
            double newUtilization = (currentLoad + orderMetrics.getTotalWeight()) / candidate.getWeightCapacity();

            if (currentLoad > 0 && newUtilization <= MAX_SAFE_UTILIZATION) {
                return Optional.of(candidate);
            }
        }

        // If no partially loaded vehicle is suitable, take the highest scoring one
        return Optional.of(candidates.get(0));
    }

    /**
     * Calculates a score for a vehicle's suitability for an order.
     *
     * @param candidate The vehicle candidate
     * @param metrics The order metrics
     * @return The score (higher is better)
     */
    private double calculateVehicleScore(VehicleCandidate candidate, OrderMetrics metrics) {
        // Calculate capacity utilization score
        double utilizationScore = calculateCapacityUtilizationScore(candidate, metrics);

        // Calculate vehicle type suitability score
        double typeScore = calculateVehicleTypeSuitabilityScore(candidate, metrics);

        // Combine scores using weighting factors
        return (utilizationScore * CAPACITY_UTILIZATION_FACTOR) +
                (typeScore * VEHICLE_TYPE_FACTOR);
    }

    /**
     * Calculates a score for capacity utilization.
     *
     * @param candidate The vehicle candidate
     * @param metrics The order metrics
     * @return The score (0-100, higher is better)
     */
    private double calculateCapacityUtilizationScore(VehicleCandidate candidate, OrderMetrics metrics) {
        double score = 0.0;

        if (candidate.getWeightCapacity() == null || metrics.getTotalWeight() == 0) {
            return 50.0; // Default middle score
        }

        double newUtilization = (candidate.getCurrentWeightLoad() + metrics.getTotalWeight()) /
                candidate.getWeightCapacity();

        // Optimal utilization is around OPTIMAL_UTILIZATION (75%)
        if (newUtilization <= OPTIMAL_UTILIZATION) {
            // Increasing score as we approach optimal
            score = (newUtilization / OPTIMAL_UTILIZATION) * 100;
        } else {
            // Decreasing score as we exceed optimal
            score = Math.max(0, 100 - ((newUtilization - OPTIMAL_UTILIZATION) * 400)); // Steeper penalty for exceeding
        }

        // Bonus for consolidating orders on partially loaded vehicles
        if (candidate.getCurrentWeightLoad() > 0 && newUtilization <= MAX_SAFE_UTILIZATION) {
            score += 20;
        }

        return score;
    }

    /**
     * Calculates a score for vehicle type suitability.
     *
     * @param candidate The vehicle candidate
     * @param metrics The order metrics
     * @return The score (0-100, higher is better)
     */
    private double calculateVehicleTypeSuitabilityScore(VehicleCandidate candidate, OrderMetrics metrics) {
        double score = 50.0; // Base score

        // Match vehicle type to order weight
        if (metrics.getTotalWeight() > 1000 && candidate.getVehicleType() == VehicleType.TRUCK) {
            score += 30; // Heavy orders better suited for trucks
        } else if (metrics.getTotalWeight() > 500 && candidate.getVehicleType() == VehicleType.TRUCK) {
            score += 20; // Medium orders can use trucks
        } else if (metrics.getTotalWeight() <= 500 && candidate.getVehicleType() == VehicleType.CAR) {
            score += 30; // Light orders better suited for cars
        }

        // Consider volume if available
        if (metrics.getTotalVolume() > 0 && candidate.getVolumeCapacity() != null) {
            double volumeUtilization = metrics.getTotalVolume() / candidate.getVolumeCapacity();
            if (volumeUtilization <= 0.8) {
                score += 10; // Good volume fit
            }
        }

        return score;
    }

    /**
     * Assigns an order to a vehicle by updating the order entity.
     *
     * @param order The order to update
     * @param vehicle The vehicle to assign
     */
    private void assignOrderToVehicle(Order order, VehicleCandidate vehicle) {
        order.setVehicleId(vehicle.getVehicleId());
        order.setVehicleType(vehicle.getVehicleType());
        order.setVehicleLicensePlate(vehicle.getVehiclePlateNumber());
        order.setDriverId(vehicle.getDriverId());
        order.setDriverName(vehicle.getDriverName());
        order.setStatus(OrderStatus.DELIVERING);

        // Save the updated order
        orderRepo.save(order);
    }

    /**
     * Updates a vehicle's load after assigning an order.
     *
     * @param vehicleId The ID of the vehicle
     * @param metrics The metrics of the order
     */
    private void updateVehicleLoad(UUID vehicleId, OrderMetrics metrics) {
        VehicleLoad vehicleLoad = vehicleLoadRepository.findByVehicleId(vehicleId)
                .orElse(new VehicleLoad(vehicleId));

        // Add order metrics to current load
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

        // Update vehicle status if necessary
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found: " + vehicleId));

        if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {
            vehicle.setStatus(VehicleStatus.ASSIGNED);
            vehicleRepository.save(vehicle);
        }

        log.debug("Updated vehicle {} load: weight={}, volume={}, items={}",
                vehicleId,
                vehicleLoad.getCurrentWeightLoad(),
                vehicleLoad.getCurrentVolumeLoad(),
                vehicleLoad.getCurrentItemCount());
    }

    /**
     * Determines if a vehicle can handle an order based on weight and volume capacity.
     *
     * @param vehicle The vehicle to check
     * @param metrics The metrics of the order
     * @return true if the vehicle can handle the order, false otherwise
     */
    private boolean canVehicleHandleOrder(Vehicle vehicle, OrderMetrics metrics) {
        // If vehicle has no capacity or metrics has no weight/volume, assume it's compatible
        if (vehicle.getWeightCapacity() == null || metrics.getTotalWeight() == 0) {
            return true;
        }

        // Check weight capacity
        double currentWeightLoad = calculateCurrentVehicleLoad(vehicle.getVehicleId());
        double remainingWeightCapacity = vehicle.getWeightCapacity() - currentWeightLoad;
        boolean weightCheck = remainingWeightCapacity >= metrics.getTotalWeight();

        // Check volume capacity if applicable
        boolean volumeCheck = true;
        if (vehicle.getVolumeCapacity() != null && metrics.getTotalVolume() > 0) {
            double currentVolumeLoad = calculateCurrentVehicleVolumeLoad(vehicle.getVehicleId());
            double remainingVolumeCapacity = vehicle.getVolumeCapacity() - currentVolumeLoad;
            volumeCheck = remainingVolumeCapacity >= metrics.getTotalVolume();
        }

        return weightCheck && volumeCheck;
    }

    /**
     * Calculates metrics for an order (weight, volume, item count).
     *
     * @param order The order to calculate metrics for
     * @return The order metrics
     */
    private OrderMetrics calculateOrderMetrics(Order order) {
        List<OrderItem> items = orderItemRepo.findAllByOrderId(order.getId());

        double totalWeight = 0.0;
        double totalVolume = 0.0;

        for (OrderItem item : items) {
            int quantity = (item.getQuantity() != null) ? item.getQuantity() : 1;
            totalWeight += item.getWeight() * quantity;

            // Calculate volume if dimensions are available
            if (item.getLength() != null && item.getWidth() != null && item.getHeight() != null) {
                double itemVolume = item.getLength() * item.getWidth() * item.getHeight();
                totalVolume += itemVolume * quantity;
            }
        }

        return new OrderMetrics(totalWeight, totalVolume, items.size());
    }

    /**
     * Calculates the current weight load on a vehicle.
     *
     * @param vehicleId The ID of the vehicle
     * @return The current weight load
     */
    private double calculateCurrentVehicleLoad(UUID vehicleId) {
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
     * Calculates the current volume load on a vehicle.
     *
     * @param vehicleId The ID of the vehicle
     * @return The current volume load
     */
    private double calculateCurrentVehicleVolumeLoad(UUID vehicleId) {
        Optional<VehicleLoad> vehicleLoadOpt = vehicleLoadRepository.findByVehicleId(vehicleId);

        if (vehicleLoadOpt.isPresent() && vehicleLoadOpt.get().getCurrentVolumeLoad() != null) {
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
        VehicleLoad vehicleLoad = vehicleLoadRepository.findByVehicleId(vehicleId)
                .orElse(new VehicleLoad(vehicleId));
        vehicleLoad.setCurrentVolumeLoad(totalVolume);
        vehicleLoadRepository.save(vehicleLoad);

        return totalVolume;
    }

    // ------------------------------------------------------------------------
    // INNER CLASSES
    // ------------------------------------------------------------------------

    /**
     * Class to store order metrics (weight, volume, item count).
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
     * Class to track vehicle remaining capacity.
     */
    private static class VehicleCapacity {
        private double remainingWeightCapacity;
        private Double remainingVolumeCapacity;

        public VehicleCapacity(double remainingWeightCapacity, Double remainingVolumeCapacity) {
            this.remainingWeightCapacity = remainingWeightCapacity;
            this.remainingVolumeCapacity = remainingVolumeCapacity;
        }

        public double getRemainingWeightCapacity() {
            return remainingWeightCapacity;
        }

        public void setRemainingWeightCapacity(double remainingWeightCapacity) {
            this.remainingWeightCapacity = remainingWeightCapacity;
        }

        public Double getRemainingVolumeCapacity() {
            return remainingVolumeCapacity;
        }

        public void setRemainingVolumeCapacity(Double remainingVolumeCapacity) {
            this.remainingVolumeCapacity = remainingVolumeCapacity;
        }
    }

    /**
     * Class representing a candidate vehicle for assignment.
     */
    private static class VehicleCandidate {
        private final UUID vehicleId;
        private final VehicleType vehicleType;
        private final String vehiclePlateNumber;
        private final Double weightCapacity;
        private final Double volumeCapacity;
        @Setter
        private double currentWeightLoad;
        @Setter
        private double currentVolumeLoad;
        private final UUID driverId;
        private final String driverName;

        public VehicleCandidate(
                UUID vehicleId,
                VehicleType vehicleType,
                String vehiclePlateNumber,
                Double weightCapacity,
                Double volumeCapacity,
                double currentWeightLoad,
                double currentVolumeLoad,
                UUID driverId,
                String driverName) {
            this.vehicleId = vehicleId;
            this.vehicleType = vehicleType;
            this.vehiclePlateNumber = vehiclePlateNumber;
            this.weightCapacity = weightCapacity;
            this.volumeCapacity = volumeCapacity;
            this.currentWeightLoad = currentWeightLoad;
            this.currentVolumeLoad = currentVolumeLoad;
            this.driverId = driverId;
            this.driverName = driverName;
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

        public Double getWeightCapacity() {
            return weightCapacity;
        }

        public Double getVolumeCapacity() {
            return volumeCapacity;
        }

        public double getCurrentWeightLoad() {
            return currentWeightLoad;
        }

        public double getCurrentVolumeLoad() {
            return currentVolumeLoad;
        }

        public UUID getDriverId() {
            return driverId;
        }

        public String getDriverName() {
            return driverName;
        }
    }
}