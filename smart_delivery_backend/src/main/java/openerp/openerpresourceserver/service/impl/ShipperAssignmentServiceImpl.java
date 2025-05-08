package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.ShipperAssignmentService;
import openerp.openerpresourceserver.utils.GAAutoAssign.GAAutoAssign;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShipperAssignmentServiceImpl implements ShipperAssignmentService {

    private final OrderRepo orderRepo;
    private final ShipperRepo shipperRepo;
    private final HubRepo hubRepo;
    private final AssignOrderShipperRepository assignOrderShipperRepository;
    private final GAAutoAssign gaAutoAssign;
    private final RecipientRepo recipientRepo;

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
        List<Employee> shipperList = new ArrayList<>();
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
        Map<UUID, List<Order>> orderShipperMap = gaAutoAssign.autoAssignOrderToEmployee(hub, orderList, shipperList);

        for (Map.Entry<UUID, List<Order>> entry : orderShipperMap.entrySet()) {
            UUID shipperId = entry.getKey();
            List<Order> assignedOrders = entry.getValue();
            int sequenceNumber = 1;

            for (Order order : assignedOrders) {
                // Create assignment
                AssignOrderShipper assignment = new AssignOrderShipper();
                assignment.setOrderId(order.getId());
                assignment.setShipperId(shipperId);
                assignment.setShipperName(shipperRepo.findById(shipperId).get().getName());
                assignment.setSequenceNumber(sequenceNumber++); // Sequence number can be set based on your logic
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
                responseDto.setShipperId(shipperId);
                responseDto.setShipperName(shipperRepo.findById(shipperId).get().getName());
                responseDto.setShipperPhone(shipperRepo.findById(shipperId).get().getPhone());
                responseDto.setStatus(OrderStatus.ASSIGNED_SHIPPER);
                result.add(responseDto);
            }
        }

        //        int shipperIndex = 0;
//        for (int i = 0; i < orderList.size(); i++) {
//            Order order = orderList.get(i);
//            Shipper shipper = shipperList.get(shipperIndex);
//
//            // Create assignment
//            AssignOrderShipper assignment = new AssignOrderShipper();
//            assignment.setOrderId(order.getId());
//            assignment.setShipperId(shipper.getId());
//            assignment.setShipperName(shipper.getName());
//            assignment.setSequenceNumber(i + 1);
//            assignment.setStatus(ShipperAssignmentStatus.ASSIGNED);
//            assignment.setDeliveryAttempts(0);
//            assignment.setCreatedBy("admin"); // Should be updated with actual user
//
//            assignments.add(assignment);
//
//            // Update order status
//            order.setStatus(OrderStatus.ASSIGNED_SHIPPER);
//            orderRepo.save(order);
//
//            // Create response DTO
//            OrderResponseCollectorShipperDto responseDto = new OrderResponseCollectorShipperDto();
//            responseDto.setId(order.getId());
//            responseDto.setShipperId(shipper.getId());
//            responseDto.setShipperName(shipper.getName());
//            responseDto.setShipperPhone(shipper.getPhone());
//            responseDto.setStatus(OrderStatus.ASSIGNED_SHIPPER);
//            result.add(responseDto);
//
//            // Move to next shipper (round-robin)
//            shipperIndex = (shipperIndex + 1) % shipperList.size();
//        }

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

    public List<TodayAssignmentShipperDto> getShipperAssignmentsTodayByHub(UUID hubId){
        // Check if hub exists
        if (!hubRepo.existsById(hubId)) {
            throw new NotFoundException("Hub not found with ID: " + hubId);
        }
        // Get all shippers
        List<Shipper> shippers = shipperRepo.findAllByHubId(hubId);
        if(shippers.isEmpty()) return new ArrayList<>();
        // Get all assignments for the hub
        List<AssignOrderShipper> assignments = new ArrayList<>();
        for(Shipper shipper : shippers){
            List<AssignOrderShipper> assignOrderShippers = assignOrderShipperRepository.findByShipperId(shipper.getId());
             assignments.addAll(assignOrderShippers);
        }

        if (assignments.isEmpty()) {
            return new ArrayList<>();
        }


        return  assignments.stream()
                .collect(Collectors.toMap(
                        AssignOrderShipper::getShipperId, // Group by collectorId
                        assignment -> TodayAssignmentShipperDto.builder()
                                .shipperId(assignment.getShipperId()) // ID của collector
                                .shipperName(assignment.getShipperName()) // Tên collector
                                .numOfOrders(countOrdersForShipper(assignments, assignment.getShipperId())) // Số đơn hàng
                                .numOfCompleted(assignments.stream()
                                        .filter(a -> a.getShipperId().equals(assignment.getShipperId()))
                                        .filter(a -> "COMPLETED".equals(a.getStatus().toString()))
                                        .count())
                                .status(assignment.getStatus().toString()) // Trạng thái
                                .build(),
                        (existing, replacement) -> existing) // Nếu có trùng lặp, giữ lại bản ghi đầu tiên
                ).values().stream() // Lấy danh sách các giá trị (TodayAssignmentDto)
                .sorted(Comparator.comparing(TodayAssignmentShipperDto::getShipperName)) // Sắp xếp theo collectorId
                .collect(Collectors.toList());
    };

    public List<AssignOrderShipperDto> getShipperAssignmentsToday(UUID shipperId){
        // Check if shipper exists
        if (!shipperRepo.existsById(shipperId)) {
            throw new NotFoundException("Shipper not found with ID: " + shipperId);
        }
        // Get all assignments for the shipper
        List<AssignOrderShipper> assignments = assignOrderShipperRepository.findByShipperId(shipperId);

        if (assignments.isEmpty()) {
            return new ArrayList<>();
        }

        return assignments.stream()
                .map(this::convertToAssignOrderShipperDto)
                .collect(Collectors.toList());
    };

    public AssignOrderShipperDto convertToAssignOrderShipperDto(AssignOrderShipper assignOrderShipper) {
        Order order = orderRepo.findById(assignOrderShipper.getOrderId()).orElse(null);
        if (order == null) {
            throw new NotFoundException("Order not found with ID: " + assignOrderShipper.getOrderId());
        }
        Recipient recipient = recipientRepo.findById(order.getRecipientId()).orElseThrow(()  -> new NotFoundException("Recipient not found with ID: " + order.getRecipientId()));
        return AssignOrderShipperDto.builder().
                 id(assignOrderShipper.getId())
                .orderId(assignOrderShipper.getOrderId())
                .shipperId(assignOrderShipper.getShipperId())
                .shipperName(assignOrderShipper.getShipperName())
                .sequenceNumber(assignOrderShipper.getSequenceNumber())
                .status(assignOrderShipper.getStatus())
                .orderStatus(order.getStatus())
                .recipientName(recipient.getName())
                .recipientPhone(recipient.getPhone())
                .recipientAddress(recipient.getAddress())
                .recipientLatitude(recipient.getLatitude())
                .recipientLongitude(recipient.getLongitude())
                .sequenceNumber(assignOrderShipper.getSequenceNumber())
                .build();

    }

//    public TodayAssignmentShipperDto convertToTodayShipperAssignmentDto(AssignOrderShipper assignOrderShipper) {
//        Order order = orderRepo.findById(assignOrderShipper.getOrderId()).orElse(null);
//        if (order == null) {
//            throw new NotFoundException("Order not found with ID: " + assignOrderShipper.getOrderId());
//        }
//        Recipient recipient = recipientRepo.findById(order.getRecipientId()).orElseThrow(()  -> new NotFoundException("Recipient not found with ID: " + order.getRecipientId()));
//        return TodayAssignmentShipperDto.builder()
//                .shipperId(assignOrderShipper.getShipperId())
//                .shipperName(assignOrderShipper.getShipperName())
//                .status(assignOrderShipper.getStatus())
//                .orderStatus(order.getStatus())
//                .recipientName(recipient.getName())
//                .recipientPhone(recipient.getPhone())
//                .recipientAddress(recipient.getAddress())
//                .recipientLatitude(recipient.getLatitude())
//                .recipientLongitude(recipient.getLongitude())
//                .sequenceNumber(assignOrderShipper.getSequenceNumber())
//                .build();
//
//    }
    private Long countOrdersForShipper(List<AssignOrderShipper> assignments, UUID shipperId) {
        return assignments.stream()
                .filter(assignment -> assignment.getShipperId().equals(shipperId)) // Lọc các assignment theo collectorId
                .count(); // Đếm số lượng
    }
}