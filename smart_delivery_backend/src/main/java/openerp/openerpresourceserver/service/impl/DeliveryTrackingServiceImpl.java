package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.AssignOrderShipper;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;
import openerp.openerpresourceserver.repository.AssignOrderShipperRepository;
import openerp.openerpresourceserver.repository.OrderRepo;
import openerp.openerpresourceserver.service.DeliveryTrackingService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryTrackingServiceImpl implements DeliveryTrackingService {

    private final OrderRepo orderRepo;
    private final AssignOrderShipperRepository assignOrderShipperRepository;

    @Override
    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status, String notes) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        // Validate status transition
        if (!OrderStatus.isValidTransition(order.getStatus(), status)) {
            throw new IllegalStateException("Invalid status transition from " + order.getStatus() + " to " + status);
        }

        // Update order status
        order.setStatus(status);
        orderRepo.save(order);

        // Find current assignment
        List<AssignOrderShipper> assignments = assignOrderShipperRepository.findByOrderId(orderId);
        AssignOrderShipper activeAssignment = assignments.stream()
                .filter(a -> a.getStatus() != ShipperAssignmentStatus.COMPLETED &&
                        a.getStatus() != ShipperAssignmentStatus.CANCELED)
                .findFirst()
                .orElse(null);

        // If there's an active assignment, update its status based on order status
        if (activeAssignment != null) {
            ShipperAssignmentStatus assignmentStatus;
            switch (status) {
                case SHIPPED:
                    assignmentStatus = ShipperAssignmentStatus.PICKED_UP;
                    break;
                case COMPLETED:
                    assignmentStatus = ShipperAssignmentStatus.DELIVERED;
                    break;
                case CANCELLED:
                    assignmentStatus = ShipperAssignmentStatus.CANCELED;
                    break;
                default:
                    assignmentStatus = ShipperAssignmentStatus.IN_TRANSIT;
            }

            activeAssignment.setStatus(assignmentStatus);
            activeAssignment.setDeliveryNotes(notes);
            assignOrderShipperRepository.save(activeAssignment);
        }
    }

    @Override
    public List<Order> getOrdersByStatusAndHub(UUID hubId, OrderStatus status) {
        // Get all orders with the given status at the specified hub
        return orderRepo.findAll().stream()
                .filter(order -> order.getFinalHubId() != null &&
                        order.getFinalHubId().equals(hubId) &&
                        order.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderStatus> getOrderStatusHistory(UUID orderId) {
        // In a real implementation, this would fetch from an order_history table
        // For this example, we'll return a predefined list based on the current status
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        List<OrderStatus> history = new ArrayList<>();
        OrderStatus currentStatus = order.getStatus();

        // Build history based on the current status
        // This is a simplified implementation; in reality, you'd query a history table
        for (OrderStatus status : OrderStatus.getFullLifecycle()) {
            history.add(status);
            if (status == currentStatus) {
                break;
            }
        }

        return history;
    }

    @Override
    public String getOrderTrackingInfo(UUID orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        // Get the current status description
        String statusDescription = order.getStatus().getDescription();

        // For orders in transit or out for delivery, get more detailed information
        if (order.getStatus() == OrderStatus.DELIVERING ||
                order.getStatus() == OrderStatus.SHIPPED) {

            // Get active assignment
            AssignOrderShipper activeAssignment = assignOrderShipperRepository.findByOrderId(orderId)
                    .stream()
                    .filter(a -> a.getStatus() == ShipperAssignmentStatus.IN_TRANSIT ||
                            a.getStatus() == ShipperAssignmentStatus.PICKED_UP)
                    .findFirst()
                    .orElse(null);

            if (activeAssignment != null) {
                return statusDescription + " - " +
                        (activeAssignment.getDeliveryNotes() != null ? activeAssignment.getDeliveryNotes() : "No additional details");
            }
        }

        return statusDescription;
    }
}