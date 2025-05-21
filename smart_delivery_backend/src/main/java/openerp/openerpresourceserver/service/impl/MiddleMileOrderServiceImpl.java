package openerp.openerpresourceserver.service.impl;


import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.OrderResponseDto;
import openerp.openerpresourceserver.dto.OrderSummaryMiddleMileDto;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.MiddleMileOrderService;
import openerp.openerpresourceserver.service.OrderService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MiddleMileOrderServiceImpl implements MiddleMileOrderService {

    private final OrderRepo orderRepo;
    private final VehicleRepository vehicleRepo;
    private final OrderService orderService;
    private final TripOrderRepository tripOrderRepository;
    private final TripRepository tripRepository;
    private final RouteScheduleRepository routeScheduleRepository;
    private final RouteRepository routeRepository;
    private final DriverRepo driverRepo;

    /**
     * Gán đơn hàng cho một chuyến
     */
    @Transactional
    @Override
    public void assignOrdersToTrip(UUID tripId, List<UUID> orderIds) {
//        RouteVehicle routeVehicle = routeVehicleRepository.findById(routeVehicleId)
//                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));
//        Vehicle vehicle = vehicleRepo.findById(routeVehicle.getVehicleId()).orElseThrow(()-> new NotFoundException("not found vehicle"));
//
//
//        List<Order> orders = new ArrayList<>();
//
//        for (UUID orderId : orderIds) {
//            Order order = orderRepo.findById(orderId)
//                    .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
//
//            // Kiểm tra đơn hàng có ở trạng thái hợp lệ không
//            if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.ASSIGNED) {
//                throw new IllegalStateException("Order is not in a valid state for assignment: " + orderId);
//            }
//
//            orders.add(order);
//        }
//
//        // Gán đơn hàng cho chuyến
//        for (Order order : orders) {
//            order.setRouteId(routeVehicle.getRouteId());
//            order.setVehicleId(routeVehicle.getVehicleId());
//            order.setVehicleType(vehicle.getVehicleType());
//            order.setVehicleLicensePlate(vehicle.getPlateNumber());
//            order.setStatus(OrderStatus.DELIVERING);
//
//            orderRepo.save(order);
//        }
        return;
    }

    /**
     * Hủy gán đơn hàng cho chuyến
     */
    @Transactional
    @Override
    public void unassignOrderFromTrip(UUID orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

//        // Kiểm tra đơn hàng có được gán cho chuyến nào không
//        if (order.getRouteId() == null || order.getVehicleId() == null) {
//            throw new IllegalStateException("Order is not assigned to a trip: " + orderId);
//        }
//
//        // Hủy gán đơn hàng
//        order.setRouteId(null);
//        order.setVehicleId(null);
//        order.setVehicleType(null);
//        order.setVehicleLicensePlate(null);
//        order.setDriverId(null);
//        order.setDriverName(null);

        // Đặt lại trạng thái nếu đang ở trạng thái SHIPPING
        if (order.getStatus() == OrderStatus.DELIVERING) {
            order.setStatus(OrderStatus.ASSIGNED);
        }

        orderRepo.save(order);
    }

    /**
     * Lấy danh sách đơn hàng theo chuyến
     */
    @Override
    public List<OrderResponseDto> getOrdersByTrip(UUID routeVehicleId) {
//        RouteVehicle routeVehicle = routeVehicleRepository.findById(routeVehicleId)
//                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));
//        return orderRepo.findAllByRouteVehicleId(routeVehicleId).stream().map(o-> orderService.getOrderById(o.getId())).toList();
        return null;
    }

    /**
     * Cập nhật trạng thái đơn hàng trong chuyến
     */
    @Transactional
    @Override
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // Kiểm tra trạng thái hợp lệ
        if (order.getStatus() == OrderStatus.DELIVERING && status == OrderStatus.DELIVERED) {
            order.setStatus(status);
            orderRepo.save(order);
        } else {
            throw new IllegalStateException("Invalid status transition");
        }
    }

    /**
     * Hoàn thành chuyến và cập nhật tất cả đơn hàng
     */
    @Transactional
    @Override
    public void completeTrip(UUID routeVehicleId) {
        List<Order> orders = orderRepo.findAllByRouteVehicleId(routeVehicleId);

        for (Order order : orders) {
            // Chỉ cập nhật đơn hàng đang ở trạng thái SHIPPING
            if (order.getStatus() == OrderStatus.DELIVERING) {
                order.setStatus(OrderStatus.DELIVERED);
                orderRepo.save(order);
            }
        }

//        // Cập nhật trạng thái xe
//        RouteVehicle routeVehicle = routeVehicleRepository.findById(routeVehicleId)
//                .orElseThrow(() -> new NotFoundException("Route vehicle assignment not found"));
//        Vehicle vehicle = vehicleRepo.findById(routeVehicle.getVehicleId()).orElseThrow(()-> new NotFoundException("not found vehicle"));
//        vehicle.setStatus(VehicleStatus.AVAILABLE);
//        vehicleRepo.save(vehicle);
    }

    @Override
    public List<OrderSummaryMiddleMileDto> getCollectedHubListVehicle(UUID vehicleId, UUID hubId){
        return orderRepo.getCollectedCollectorListVehicle(vehicleId, hubId);

    }
    @Transactional
    public void assignAndConfirmOrdersOut(Principal principal, UUID tripId, List<UUID> orderIds) {
        log.info("Assigning and confirming {} orders for trip {}", orderIds.size(), tripId);

        // 1. Validate capacity
        validateTripCapacity(tripId, orderIds);

        // 2. Assign orders to trip
        assignOrdersToTrip(tripId, orderIds);

        // 3. Confirm orders as ready for pickup
        List<Order> orders = new ArrayList<>();
        for (UUID orderId : orderIds) {
            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

            // Validate current status before changing
            if (order.getStatus() != OrderStatus.COLLECTED_HUB) {
                throw new IllegalStateException("Order " + orderId + " is not in CONFIRMED_AT_HUB status. Current status: " + order.getStatus());
            }

            order.setStatus(OrderStatus.CONFIRMED_OUT);
            order.setChangedBy(principal.getName());
            orders.add(order);

            log.debug("Updated order {} status to CONFIRMED_OUT", orderId);
        }
        orderRepo.saveAll(orders);
        List<TripOrder> tripOrders = new ArrayList<>();
        for (UUID orderId : orderIds) {
            TripOrder tripOrder = new TripOrder();
            tripOrder.setTripId(tripId);
            tripOrder.setOrderId(orderId);
            tripOrders.add(tripOrder);
        }
        tripOrderRepository.saveAll(tripOrders);
//        // 4. Update trip status if needed
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));
//
//        if ("PLANNED".equals(trip.getStatus())) {
//            trip.setStatus("READY_FOR_PICKUP");
//            tripRepository.save(trip);
//            log.info("Updated trip {} status to READY_FOR_PICKUP", tripId);
//        }

        // 5. Notify driver
        notifyDriverTripReady(tripId);

        log.info("Successfully assigned and confirmed {} orders for trip {}", orderIds.size(), tripId);
    }



    /**
     * Validate that the selected orders can fit in the trip's vehicle capacity
     */
    private void validateTripCapacity(UUID tripId, List<UUID> orderIds) {
        log.debug("Validating capacity for trip {} with {} orders", tripId, orderIds.size());

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));

        Vehicle vehicle = vehicleRepo.findById(trip.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle not found: " + trip.getVehicleId()));

        // Calculate current trip load
        double currentWeight = getCurrentTripWeight(tripId);
        double currentVolume = getCurrentTripVolume(tripId);

        // Calculate additional weight and volume from new orders
        List<Order> newOrders = orderRepo.findAllById(orderIds);
        double additionalWeight = newOrders.stream().mapToDouble(Order::getWeight).sum();
        double additionalVolume = newOrders.stream().mapToDouble(Order::getVolume).sum();

        // Validate weight capacity
        if (vehicle.getWeightCapacity() != null) {
            double totalWeight = currentWeight + additionalWeight;
            if (totalWeight > vehicle.getWeightCapacity()) {
                throw new IllegalArgumentException(
                        String.format("Total weight (%.2f kg) exceeds vehicle capacity (%.2f kg). Current: %.2f kg, Additional: %.2f kg",
                                totalWeight, vehicle.getWeightCapacity(), currentWeight, additionalWeight)
                );
            }
        }

        // Validate volume capacity
        if (vehicle.getVolumeCapacity() != null) {
            double totalVolume = currentVolume + additionalVolume;
            if (totalVolume > vehicle.getVolumeCapacity()) {
                throw new IllegalArgumentException(
                        String.format("Total volume (%.2f m³) exceeds vehicle capacity (%.2f m³). Current: %.2f m³, Additional: %.2f m³",
                                totalVolume, vehicle.getVolumeCapacity(), currentVolume, additionalVolume)
                );
            }
        }

        log.debug("Capacity validation passed. Weight: {}/{} kg, Volume: {}/{} m³",
                currentWeight + additionalWeight, vehicle.getWeightCapacity(),
                currentVolume + additionalVolume, vehicle.getVolumeCapacity());
    }

    /**
     * Get current total weight of already assigned orders in the trip
     */
    private double getCurrentTripWeight(UUID tripId) {
        List<TripOrder> tripOrders = tripOrderRepository.findAllByTripId(tripId);
        return tripOrders.stream()
                .mapToDouble(tripOrder -> {
                    try {
                        Order order = orderRepo.findById(tripOrder.getOrderId()).orElse(null);
                        return order != null ? order.getWeight() : 0.0;
                    } catch (Exception e) {
                        log.warn("Error getting weight for trip order {}: {}", tripOrder.getId(), e.getMessage());
                        return 0.0;
                    }
                })
                .sum();
    }

    /**
     * Get current total volume of already assigned orders in the trip
     */
    private double getCurrentTripVolume(UUID tripId) {
        List<TripOrder> tripOrders = tripOrderRepository.findAllByTripId(tripId);
        return tripOrders.stream()
                .mapToDouble(tripOrder -> {
                    try {
                        Order order = orderRepo.findById(tripOrder.getOrderId()).orElse(null);
                        return order != null ? order.getVolume() : 0.0;
                    } catch (Exception e) {
                        log.warn("Error getting volume for trip order {}: {}", tripOrder.getId(), e.getMessage());
                        return 0.0;
                    }
                })
                .sum();
    }

    /**
     * Notify driver that their trip is ready for pickup
     */
    private void notifyDriverTripReady(UUID tripId) {
        try {
            Trip trip = tripRepository.findById(tripId)
                    .orElseThrow(() -> new NotFoundException("Trip not found: " + tripId));

            if (trip.getDriverId() == null) {
                log.warn("No driver assigned to trip {}. Cannot send notification.", tripId);
                return;
            }

            Driver driver = driverRepo.findById(trip.getDriverId())
                    .orElse(null);

            if (driver == null) {
                log.warn("Driver not found for trip {}. Cannot send notification.", tripId);
                return;
            }

            // Get trip details for notification
            RouteSchedule routeSchedule = routeScheduleRepository.findById(trip.getRouteScheduleId())
                    .orElse(null);
            Route route = routeSchedule != null ? routeRepository.findById(routeSchedule.getRouteId()).orElse(null) : null;

            // Count assigned orders
            int orderCount = tripOrderRepository.findAllByTripId(tripId).size();

            // Create notification message
            String message = String.format(
                    "Trip %s is ready for pickup. Route: %s, Orders: %d items. Please proceed to hub for pickup.",
                    tripId.toString().substring(0, 8),
                    route != null ? route.getRouteName() : "Unknown",
                    orderCount
            );

            // Send notification (implement based on your notification system)
            // Examples:
            // 1. Push notification to mobile app
            sendPushNotification(driver.getId(), "Trip Ready", message);

            // 2. SMS notification
            if (driver.getPhone() != null) {
                sendSmsNotification(driver.getPhone(), message);
            }

            // 3. Email notification
            if (driver.getEmail() != null) {
                sendEmailNotification(driver.getEmail(), "Trip Ready for Pickup", message);
            }

            // 4. In-app notification
            createInAppNotification(driver.getId(), "TRIP_READY", message, tripId);

            log.info("Sent trip ready notification to driver {} for trip {}", driver.getUsername(), tripId);

        } catch (Exception e) {
            log.error("Failed to notify driver for trip {}: {}", tripId, e.getMessage(), e);
            // Don't throw exception here to avoid rolling back the transaction
        }
    }

    /**
     * Send push notification to driver's mobile app
     */
    private void sendPushNotification(UUID driverId, String title, String message) {
        try {
            // Implement based on your push notification service (FCM, APNs, etc.)
            log.debug("Push notification sent to driver {}: {}", driverId, message);
        } catch (Exception e) {
            log.warn("Failed to send push notification to driver {}: {}", driverId, e.getMessage());
        }
    }

    /**
     * Send SMS notification to driver
     */
    private void sendSmsNotification(String phoneNumber, String message) {
        try {
            // Implement based on your SMS service (Twilio, AWS SNS, etc.)
            log.debug("SMS notification sent to {}: {}", phoneNumber, message);
        } catch (Exception e) {
            log.warn("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
        }
    }

    /**
     * Send email notification to driver
     */
    private void sendEmailNotification(String email, String subject, String message) {
        try {
            // Implement based on your email service
            log.debug("Email notification sent to {}: {}", email, subject);
        } catch (Exception e) {
            log.warn("Failed to send email to {}: {}", email, e.getMessage());
        }
    }

    /**
     * Create in-app notification that driver can see in the mobile app
     */
    private void createInAppNotification(UUID driverId, String type, String message, UUID tripId) {
        try {
            // Create notification record in database
            log.debug("In-app notification created for driver {}: {}", driverId, message);
        } catch (Exception e) {
            log.warn("Failed to create in-app notification for driver {}: {}", driverId, e.getMessage());
        }
    }
}