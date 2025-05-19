package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.*;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.AssignmentService;
import openerp.openerpresourceserver.utils.DistanceCalculator.HaversineDistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired private TripRepository tripRepository;
    @Autowired private TripOrderRepository tripOrderRepository;
    @Autowired private TripItemRepository tripItemRepository;

    @Autowired private RouteScheduleRepository routeScheduleRepository;
    @Autowired private RouteStopRepository routeStopRepository;



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
     * @param principal
     * @param assignmentId The ID of the assignment to update
     * @param status       The new status to set
     * @throws NotFoundException if the assignment is not found
     */
    public void updateAssignmentStatus(Principal principal, UUID assignmentId, CollectorAssignmentStatus status) {
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

    @Override
    @Transactional
    public void decreaseVehicleLoad(UUID vehicleId, UUID orderId) {
        log.info("Decreasing vehicle {} load for completed order {}", vehicleId, orderId);

        // Get order metrics
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        // Update vehicle load
        VehicleLoad vehicleLoad = vehicleLoadRepository.findByVehicleId(vehicleId)
                .orElse(new VehicleLoad(vehicleId));

        // Subtract the order's metrics from the current load

        List<OrderItem> items = orderItemRepo.findAllByOrderId(order.getId());

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        int quantity = items.size();
        totalWeight += order.getWeight();
        totalVolume += order.getLength() * order.getWidth() * order.getHeight() / 1000000; //m3
        vehicleLoad.setCurrentVolumeLoad(Math.max(0, vehicleLoad.getCurrentVolumeLoad() - totalVolume));
        vehicleLoad.setCurrentWeightLoad(Math.max(0, vehicleLoad.getCurrentWeightLoad() - totalWeight));
        vehicleLoad.setCurrentItemCount(Math.max(0, vehicleLoad.getCurrentItemCount() - quantity));
        // Save updated load
        vehicleLoadRepository.save(vehicleLoad);
//
//        // If vehicle is now empty, update status to AVAILABLE
//        if (newWeight == 0) {
//            Vehicle vehicle = vehicleRepository.findById(vehicleId)
//                    .orElseThrow(() -> new NotFoundException("Vehicle not found: " + vehicleId));
//
//            if (vehicle.getStatus() == VehicleStatus.ASSIGNED) {
//                vehicle.setStatus(VehicleStatus.AVAILABLE);
//                vehicleRepository.save(vehicle);
//            }
//        }

        log.info("Updated vehicle {} load after removing order {}: weight={}, volume={}, items={}",
                vehicleId, orderId,
                vehicleLoad.getCurrentWeightLoad(),
                vehicleLoad.getCurrentVolumeLoad(),
                vehicleLoad.getCurrentItemCount());
    }

    @Transactional
    @Override
    public void assignOrderItemsToTrip(UUID hubId) {
        List<Order> pendingOrders = orderRepo.findAllByOriginHubIdAndStatus(hubId, OrderStatus.COLLECTED_HUB);
        if (pendingOrders.isEmpty()) {
            log.warn("No pending order item found for this hub");
        } else {
            log.warn("There are manys {}");
            List<Trip> availableTrips = tripRepository.findAllTripsByHubIdAndStatusAndDate(hubId, TripStatus.PLANNED.name(), LocalDate.now());
            if(!availableTrips.isEmpty()){
                log.warn("not filtered trips");
            }
            pendingOrders.sort(Comparator.comparing(Order::getCreatedAt));
            for (Order order : pendingOrders) {
                List<Trip> filteredTrips = filterTripAlignedWithOrder(order, availableTrips);
                if (filteredTrips.isEmpty()) {
                    log.warn("No available trips found for order {}", order.getId());
                } else {


                    filteredTrips.sort(Comparator.comparing(trip -> {
                        RouteSchedule routeSchedule = routeScheduleRepository.findById(trip.getRouteScheduleId())
                                .orElseThrow(() -> new NotFoundException("Route schedule not found"));

                        // Kết hợp với timezone cụ thể
                        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
                        LocalDateTime combinedDateTime = trip.getDate().atTime(routeSchedule.getStartTime());
                        return combinedDateTime.atZone(zoneId).toInstant();
                    }));
                    Trip bestTrip = filteredTrips.getFirst();
                    TripOrder tripOrder = TripOrder.builder()
                            .orderId(order.getId())
                            .tripId(bestTrip.getId()).build();
                    tripOrderRepository.save(tripOrder);
                }
            }
        }
    }

    public List<Trip> filterTripAlignedWithOrder(Order order, List<Trip> availableTrips) {
        UUID originHub = order.getOriginHubId();
        UUID finalHub = order.getFinalHubId();
        List<Trip> filteredTrips = new ArrayList<>();
        for (Trip trip : availableTrips) {
            RouteSchedule routeSchedule = routeScheduleRepository.findById(trip.getRouteScheduleId())
                    .orElseThrow(() -> new NotFoundException("Route schedule not found"));;
            List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequence(routeSchedule.getRouteId());
            List<UUID> hubIds = routeStops.stream().map(RouteStop::getHubId).collect(Collectors.toList());
            if (hubIds.contains(originHub) && hubIds.contains(finalHub) && hubIds.indexOf(originHub) < hubIds.indexOf(finalHub)) {
                filteredTrips.add(trip);
            }
        }
        return filteredTrips;
    }
    @Override
    public void assignOrderItemsForTripsForAllHubs(){
        List<Hub> hubs = hubRepo.findAll();
        for (Hub hub : hubs) {
            assignOrderItemsToTrip(hub.getHubId());
        }
    }
}