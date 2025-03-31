package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.EmployeeDTO;
import openerp.openerpresourceserver.dto.OrderRequestDto;
import openerp.openerpresourceserver.dto.OrderResponseCollectorShipperDto;
import openerp.openerpresourceserver.dto.OrderSummaryDTO;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;
import openerp.openerpresourceserver.repository.AssignOrderShipperRepository;
import openerp.openerpresourceserver.repository.HubRepo;
import openerp.openerpresourceserver.repository.OrderRepo;
import openerp.openerpresourceserver.repository.ShipperRepo;
import openerp.openerpresourceserver.service.ShipperAssignmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShipperAssignmentServiceImpl implements ShipperAssignmentService {

    private final OrderRepo orderRepo;
    private final ShipperRepo shipperRepo;
    private final HubRepo hubRepo;
    private final AssignOrderShipperRepository assignOrderShipperRepository;

    @Override
    public List<OrderSummaryDTO> getPendingDeliveryOrders(UUID hubId) {
        // Get orders that have arrived at the destination hub and are ready for delivery
        List<Order> orders = orderRepo.findAll().stream()
                .filter(order -> order.getFinalHubId() != null &&
                        order.getFinalHubId().equals(hubId) &&
                        order.getStatus() == OrderStatus.DELIVERED)
                .collect(Collectors.toList());

        return orders.stream()
                .map(OrderSummaryDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<OrderResponseCollectorShipperDto> assignOrdersToShippers(UUID hubId, List<OrderRequestDto> orders, List<EmployeeDTO> shippers) {
        if (hubId == null || orders.isEmpty() || shippers.isEmpty()) {
            throw new IllegalArgumentException("Missing required data for shipper assignment");
        }

        // Find the hub
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new NotFoundException("Hub not found with ID: " + hubId));

        // Get shipper and order details
        List<Shipper> shipperList = new ArrayList<>();
        for (EmployeeDTO shipperDto : shippers) {
            Shipper shipper = shipperRepo.findById(shipperDto.getId())
                    .orElseThrow(() -> new NotFoundException("Shipper not found with ID: " + shipperDto.getId()));
            shipperList.add(shipper);
        }

        List<Order> orderList = new ArrayList<>();
        for (OrderRequestDto orderDto : orders) {
            Order order = orderRepo.findById(orderDto.getId())
                    .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderDto.getId()));
            orderList.add(order);
        }

        // Simple round-robin assignment algorithm
        List<OrderResponseCollectorShipperDto> result = new ArrayList<>();
        List<AssignOrderShipper> assignments = new ArrayList<>();

        int shipperIndex = 0;
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            Shipper shipper = shipperList.get(shipperIndex);

            // Create assignment
            AssignOrderShipper assignment = new AssignOrderShipper();
            assignment.setOrderId(order.getId());
            assignment.setShipperId(shipper.getId());
            assignment.setShipperName(shipper.getName());
            assignment.setSequenceNumber(i + 1);
            assignment.setStatus(ShipperAssignmentStatus.ASSIGNED);
            assignment.setDeliveryAttempts(0);
            assignment.setCreatedBy("admin"); // Should be updated with actual user

            assignments.add(assignment);

            // Update order status
            order.setStatus(OrderStatus.ASSIGNED_SHIPPER);
            orderRepo.save(order);

            // Create response DTO
            OrderResponseCollectorShipperDto responseDto = new OrderResponseCollectorShipperDto();
            responseDto.setId(order.getId());
            responseDto.setShipperId(shipper.getId());
            responseDto.setShipperName(shipper.getName());
            responseDto.setShipperPhone(shipper.getPhone());
            responseDto.setStatus(OrderStatus.ASSIGNED_SHIPPER);
            result.add(responseDto);

            // Move to next shipper (round-robin)
            shipperIndex = (shipperIndex + 1) % shipperList.size();
        }

        // Save all assignments
        assignOrderShipperRepository.saveAll(assignments);

        return result;
    }

    @Override
    public List<OrderSummaryDTO> getShipperAssignments(UUID shipperId) {
        // Check if shipper exists
        if (!shipperRepo.existsById(shipperId)) {
            throw new NotFoundException("Shipper not found with ID: " + shipperId);
        }

        // Get all active assignments for the shipper
        List<AssignOrderShipper> assignments = assignOrderShipperRepository.findByShipperId(shipperId);

        if (assignments.isEmpty()) {
            return new ArrayList<>();
        }

        // Get order details for all assignments
        List<UUID> orderIds = assignments.stream()
                .map(AssignOrderShipper::getOrderId)
                .collect(Collectors.toList());

        List<Order> orders = orderRepo.findAllById(orderIds);

        return orders.stream()
                .map(OrderSummaryDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateAssignmentStatus(UUID assignmentId, ShipperAssignmentStatus status) {
        AssignOrderShipper assignment = assignOrderShipperRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with ID: " + assignmentId));

        assignment.setStatus(status);

        // If delivery failed, increment attempt counter
        if (status == ShipperAssignmentStatus.FAILED_ATTEMPT) {
            assignment.setDeliveryAttempts(assignment.getDeliveryAttempts() + 1);
        }

        assignOrderShipperRepository.save(assignment);
    }
}