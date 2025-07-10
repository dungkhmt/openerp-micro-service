package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.DeliveryRouteDTO;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.RouteOptimizationService;
import openerp.openerpresourceserver.utils.DistanceCalculator.GraphHopperCalculator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteOptimizationServiceImpl implements RouteOptimizationService {

    private final AssignOrderShipperRepository assignOrderShipperRepository;
    private final OrderRepo orderRepo;
    private final RecipientRepo recipientRepo;
    private final ShipperRepo shipperRepo;
    private final HubRepo hubRepo;
    private final GraphHopperCalculator graphHopperCalculator;

    @Override
    public DeliveryRouteDTO getOptimizedRoute(UUID shipperId) {
        Shipper shipper = shipperRepo.findById(shipperId)
                .orElseThrow(() -> new NotFoundException("Shipper not found with ID: " + shipperId));

        // Get active assignments for the shipper
        List<AssignOrderShipper> assignments = assignOrderShipperRepository.findByShipperId(shipperId).stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.ASSIGNED ||
                        a.getStatus() == ShipperAssignmentStatus.PICKED_UP ||
                        a.getStatus() == ShipperAssignmentStatus.IN_TRANSIT)
                .collect(Collectors.toList());

        if (assignments.isEmpty()) {
            throw new NotFoundException("No active assignments found for shipper");
        }

        return generateOptimizedRoute(assignments, shipper.getHubId());
    }

    @Override
    public DeliveryRouteDTO generateOptimizedRoute(List<AssignOrderShipper> assignments, UUID hubId) {
        if (assignments.isEmpty()) {
            throw new IllegalArgumentException("No assignments provided for route optimization");
        }

        // Get orders and recipients
        List<UUID> orderIds = assignments.stream()
                .map(AssignOrderShipper::getOrderId)
                .collect(Collectors.toList());

        List<Order> orders = orderRepo.findAllById(orderIds);
        Map<UUID, Order> orderMap = orders.stream()
                .collect(Collectors.toMap(Order::getId, o -> o));

        List<UUID> recipientIds = orders.stream()
                .map(Order::getRecipientId)
                .collect(Collectors.toList());

        List<Recipient> recipients = recipientRepo.findAllById(recipientIds);
        Map<UUID, Recipient> recipientMap = recipients.stream()
                .collect(Collectors.toMap(Recipient::getRecipientId, r -> r));

        // Get hub coordinates
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new NotFoundException("Hub not found"));

        // Find the shipper ID from the first assignment (all should have the same shipper)
        UUID shipperId = assignments.get(0).getShipperId();
        String shipperName = assignments.get(0).getShipperName();

        // Implement a simple nearest neighbor algorithm for TSP
        List<DeliveryRouteDTO.DeliveryStopDTO> stops = new ArrayList<>();
        double totalDistance = 0.0;

        // Start from the hub
        double currentLat = hub.getLatitude();
        double currentLon = hub.getLongitude();

        // Create a list of unvisited locations (recipients)
        List<UUID> unvisitedOrders = new ArrayList<>(orderIds);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime currentTime = LocalDateTime.now();

        // Process each stop
        int sequenceNumber = 1;
        while (!unvisitedOrders.isEmpty()) {
            // Find nearest unvisited location
            UUID nearestOrderId = null;
            double shortestDistance = Double.MAX_VALUE;

            for (UUID orderId : unvisitedOrders) {
                Order order = orderMap.get(orderId);
                Recipient recipient = recipientMap.get(order.getRecipientId());

                if (recipient.getLatitude() == null || recipient.getLongitude() == null) {
                    continue;
                }

                double distance;
                try {
                    distance = graphHopperCalculator.calculateRealDistanceFromTo(
                            currentLat, currentLon,
                            recipient.getLatitude(), recipient.getLongitude());
                } catch (Exception e) {
                    // Fallback to euclidean distance
                    distance = Math.sqrt(
                            Math.pow(currentLat - recipient.getLatitude(), 2) +
                                    Math.pow(currentLon - recipient.getLongitude(), 2)
                    ) * 111000; // Rough conversion to meters
                }

                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    nearestOrderId = orderId;
                }
            }

            if (nearestOrderId == null) {
                break; // No valid locations found
            }

            // Add nearest location to route
            Order order = orderMap.get(nearestOrderId);
            Recipient recipient = recipientMap.get(order.getRecipientId());

            // Calculate travel time (assuming 30 km/h = 500 m/min)
            int travelTimeMinutes = (int) (shortestDistance / 500);
            currentTime = currentTime.plusMinutes(travelTimeMinutes);

            // Add stop to route
            DeliveryRouteDTO.DeliveryStopDTO stop = DeliveryRouteDTO.DeliveryStopDTO.builder()
                    .orderId(order.getId())
                    .sequenceNumber(sequenceNumber++)
                    .recipientName(recipient.getName())
                    .recipientAddress(recipient.getAddress())
                    .recipientPhone(recipient.getPhone())
                    .recipientLatitude(recipient.getLatitude())
                    .recipientLongitude(recipient.getLongitude())
                    .distanceFromPreviousStop(shortestDistance)
                    .estimatedDeliveryTime(currentTime.format(timeFormatter))
                    .build();

            stops.add(stop);

            // Update current position and remove visited location
            currentLat = recipient.getLatitude();
            currentLon = recipient.getLongitude();
            unvisitedOrders.remove(nearestOrderId);

            // Add distance to total
            totalDistance += shortestDistance;

            // Add 10 minutes for delivery
            currentTime = currentTime.plusMinutes(10);
        }

        // Create and return route DTO
        return DeliveryRouteDTO.builder()
                .shipperId(shipperId)
                .shipperName(shipperName)
                .stops(stops)
                .totalDistance(totalDistance)
                .estimatedDuration((int) (totalDistance / 500) + stops.size() * 10)
                .startingHub(hub.getName())
                .hubId(hub.getHubId())
                .build();
    }

    @Override
    public String calculateEstimatedDeliveryTime(UUID orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        // Get active assignment
        AssignOrderShipper assignment = assignOrderShipperRepository.findByOrderId(orderId)
                .stream()
                .filter(a -> a.getStatus() != ShipperAssignmentStatus.COMPLETED &&
                        a.getStatus() != ShipperAssignmentStatus.CANCELED)
                .findFirst()
                .orElse(null);

        if (assignment == null) {
            return "Not yet assigned for delivery";
        }

        // Get shipper's current route
        List<AssignOrderShipper> assignments = assignOrderShipperRepository.findByShipperId(assignment.getShipperId())
                .stream()
                .filter(a -> a.getStatus() != ShipperAssignmentStatus.COMPLETED &&
                        a.getStatus() != ShipperAssignmentStatus.CANCELED)
                .collect(Collectors.toList());

        // Generate optimized route
        DeliveryRouteDTO route = generateOptimizedRoute(assignments, order.getFinalHubId());

        // Find this order in the route
        for (DeliveryRouteDTO.DeliveryStopDTO stop : route.getStops()) {
            if (stop.getOrderId().equals(orderId)) {
                return stop.getEstimatedDeliveryTime();
            }
        }

        return "Estimated time not available";
    }

    @Override
    @Transactional
    public void optimizeAssignmentSequence(UUID shipperId) {
        // Get all active assignments for this shipper
        List<AssignOrderShipper> assignments = assignOrderShipperRepository.findByShipperId(shipperId)
                .stream()
                .filter(a -> a.getStatus() == ShipperAssignmentStatus.ASSIGNED)
                .collect(Collectors.toList());

        if (assignments.isEmpty()) {
            return;
        }

        // Get the hub ID from the shipper
        Shipper shipper = shipperRepo.findById(shipperId)
                .orElseThrow(() -> new NotFoundException("Shipper not found"));

        // Generate optimized route
        DeliveryRouteDTO route = generateOptimizedRoute(assignments, shipper.getHubId());

        // Update sequence numbers based on optimized route
        Map<UUID, Integer> orderSequence = new HashMap<>();
        for (int i = 0; i < route.getStops().size(); i++) {
            DeliveryRouteDTO.DeliveryStopDTO stop = route.getStops().get(i);
            orderSequence.put(stop.getOrderId(), i + 1);
        }

        // Update assignments with new sequence numbers
        for (AssignOrderShipper assignment : assignments) {
            Integer newSequence = orderSequence.get(assignment.getOrderId());
            if (newSequence != null) {
                assignment.setSequenceNumber(newSequence);
            }
        }

        // Save updated assignments
        assignOrderShipperRepository.saveAll(assignments);
    }
}