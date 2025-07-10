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
import openerp.openerpresourceserver.service.NotificationsService;
import openerp.openerpresourceserver.service.ShipperAssignmentService;
import openerp.openerpresourceserver.utils.GAAutoAssign.GAAutoAssign;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final SenderRepo senderRepo;
    private final NotificationsService notificationsService;

    @Override
    public List<OrderResponseCollectorShipperDto> suggestOrdersToShippers(
            UUID hubId,
            List<OrderRequestDto> orders,
            List<EmployeeDTO> shippers) {

        if (hubId == null || orders.isEmpty() || shippers.isEmpty()) {
            throw new IllegalArgumentException("Missing required data for shipper assignment suggestion");
        }

        // Tìm hub
        Hub hub = hubRepo.findById(hubId)
                .orElseThrow(() -> new NotFoundException("Hub not found"));

        // Chuẩn bị dữ liệu
        List<Employee> shipperList = new ArrayList<>();
        for (EmployeeDTO shipperDto : shippers) {
            Shipper shipper = shipperRepo.findById(shipperDto.getId())
                    .orElseThrow(() -> new NotFoundException("Shipper not found"));
            shipperList.add(shipper);
        }

        List<Order> orderList = new ArrayList<>();
        for (OrderRequestDto orderDto : orders) {
            Order order = orderRepo.findById(orderDto.getId())
                    .orElseThrow(() -> new NotFoundException("Order not found"));
            orderList.add(order);
        }

        // Sử dụng thuật toán để tạo đề xuất (KHÔNG lưu vào DB)
        Map<UUID, List<Order>> orderShipperMap = gaAutoAssign.autoAssignOrderToEmployee(
                hub, orderList, shipperList);

        // Tạo response với thông tin đề xuất
        List<OrderResponseCollectorShipperDto> suggestions = new ArrayList<>();
        for (Map.Entry<UUID, List<Order>> entry : orderShipperMap.entrySet()) {
            UUID shipperId = entry.getKey();
            String shipperName = shipperRepo.findById(shipperId).get().getName();
            int sequenceNumber = 1;

            for (Order order : entry.getValue()) {
                OrderResponseCollectorShipperDto suggestion = OrderResponseCollectorShipperDto.builder()
                        .id(order.getId())
                        .shipperId(shipperId)
                        .shipperName(shipperName)
                        .sequenceNumber(sequenceNumber++)
                        .senderName(order.getSenderName())
                        .recipientName(order.getRecipientName())
                        .status(OrderStatus.DELIVERED) // Trạng thái hiện tại của order
                        .build();
                suggestions.add(suggestion);
            }
        }

        return suggestions;
    }

    @Override
    @Transactional
    public List<OrderResponseCollectorShipperDto> confirmOrdersToShippers(
            Principal principal,
            UUID hubId,
            List<ConfirmAssignmentDto.AssignmentDetailDto> assignments) {

        if (assignments.isEmpty()) {
            throw new RuntimeException("No assignments to confirm");
        }

        List<AssignOrderShipper> assignOrderShippers = new ArrayList<>();
        List<Order> ordersToUpdate = new ArrayList<>();
        List<OrderResponseCollectorShipperDto> responses = new ArrayList<>();

        for (ConfirmAssignmentDto.AssignmentDetailDto assignment : assignments) {
            // Tìm order
            Order order = orderRepo.findById(assignment.getOrderId())
                    .orElseThrow(() -> new NotFoundException("Order not found"));

            // Cập nhật trạng thái order
            order.setStatus(OrderStatus.ASSIGNED_SHIPPER);
            order.setChangedBy(principal.getName());
            ordersToUpdate.add(order);

            // Tạo AssignOrderShipper
            AssignOrderShipper assignOrderShipper = new AssignOrderShipper();
            assignOrderShipper.setOrderId(assignment.getOrderId());
            assignOrderShipper.setShipperId(assignment.getEmployeeId());
            assignOrderShipper.setShipperName(assignment.getEmployeeName());
            assignOrderShipper.setSequenceNumber(assignment.getSequenceNumber());
            assignOrderShipper.setStatus(ShipperAssignmentStatus.ASSIGNED);
            assignOrderShipper.setDeliveryAttempts(0);
            assignOrderShipper.setCreatedBy(principal.getName());
            assignOrderShippers.add(assignOrderShipper);

            // Tạo response
            OrderResponseCollectorShipperDto response = OrderResponseCollectorShipperDto.builder()
                    .id(order.getId())
                    .shipperId(assignment.getEmployeeId())
                    .shipperName(assignment.getEmployeeName())
                    .status(OrderStatus.ASSIGNED_SHIPPER)
                    .build();
            responses.add(response);
        }

        // Lưu tất cả vào database
        orderRepo.saveAll(ordersToUpdate);
        assignOrderShipperRepository.saveAll(assignOrderShippers);

        return responses;
    }
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
    public void updateAssignmentStatus(Principal principal, UUID assignmentId, ShipperAssignmentStatus status) {
        AssignOrderShipper assignment = assignOrderShipperRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with ID: " + assignmentId));
        Order order = orderRepo.findById(assignment.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + assignment.getOrderId()));
        assignment.setStatus(status);
        if(status == ShipperAssignmentStatus.COMPLETED) {
           order.setChangedBy(principal.getName());
           order.setStatus(OrderStatus.COMPLETED);
           sendDeliveryNotification(order);
        }
        else if(status == ShipperAssignmentStatus.FAILED_ATTEMPT) {
            order.setChangedBy(principal.getName());
            order.setStatus(OrderStatus.SHIPPED_FAILED);
            order.setShipAttemptCount(order.getShipAttemptCount() + 1);

            // Send notification to sender about failed delivery

            // If this is the third attempt, mark as cancelled and notify
//            if(order.getShipAttemptCount() >= 2) {
//                order.setStatus(OrderStatus.CANCELLED_SHIP);
//                sendDeliveryCancelledNotification(order);
//            }
//            else{
//                sendFailedDeliveryNotification(order, order.getShipAttemptCount());
//
//            }
        }
        else if(status == ShipperAssignmentStatus.CANCELED) {
            order.setChangedBy(principal.getName());
            order.setStatus(OrderStatus.CANCELLED_SHIP);

            // Send notification to sender about cancelled delivery
            sendDeliveryCancelledNotification(order);
        }

        else if(status == ShipperAssignmentStatus.RETURNED_TO_HUB) {
            order.setChangedBy(principal.getName());
            order.setStatus(OrderStatus.RETURNED_HUB_AFTER_SHIP_FAIL);

            // Send notification to sender about order being returned to hub
            sendReturnedToHubNotification(order);
        }
        assignOrderShipperRepository.save(assignment);
        orderRepo.save(order);
    }
    /**
     * Sends a notification to the sender that their order has been successfully delivered
     */
    private void sendDeliveryNotification(Order order) {
        try {
            Sender sender = senderRepo.findById(order.getSenderId())
                    .orElseThrow(() -> new NotFoundException("Sender not found with ID: " + order.getSenderId()));

            String senderUsername = order.getCreatedBy();
            String message = "Chúc mừng! Đơn hàng của bạn đã được giao thành công.";
            String url = "/order/view/" + order.getId();

            notificationsService.create(
                    "SYSTEM", // fromUser
                    senderUsername, // toUser (use email or ID)
                    message,
                    url
            );

            log.info("Sent delivery completion notification for order {} to sender {}",
                    order.getId(), order.getSenderId());
        } catch (Exception e) {
            log.error("Failed to send delivery notification for order {}: {}",
                    order.getId(), e.getMessage(), e);
        }
    }

    /**
     * Sends a notification to the sender that their order delivery attempt has failed
     */
    private void sendFailedDeliveryNotification(Order order, int attemptNumber) {
        try {
            Sender sender = senderRepo.findById(order.getSenderId())
                    .orElseThrow(() -> new NotFoundException("Sender not found with ID: " + order.getSenderId()));

            String senderUsername = order.getCreatedBy();
            String message = "Delivery attempt #" + attemptNumber + " was unsuccessful. ";

            if (attemptNumber < 3) {
                message += "Our shipper will try again soon.";
            } else {
                message += "We've reached the maximum number of attempts.";
            }

            String url = "/order/view/" + order.getId();

            notificationsService.create(
                    "SYSTEM", // fromUser
                    senderUsername, // toUser
                    message,
                    url
            );

            log.info("Sent failed delivery notification (attempt #{}) for order {} to sender {}",
                    attemptNumber, order.getId(), order.getSenderId());
        } catch (Exception e) {
            log.error("Failed to send failed delivery notification for order {}: {}",
                    order.getId(), e.getMessage(), e);
        }
    }

    /**
     * Sends a notification to the sender that their order delivery has been cancelled
     */
    private void sendDeliveryCancelledNotification(Order order) {
        try {

            String senderUsername = order.getCreatedBy();
            String message = "We regret to inform you that your order delivery has been cancelled. " +
                    "Please contact customer service for more information.";
            String url = "/order/view/" + order.getId();

            notificationsService.create(
                    "SYSTEM", // fromUser
                    senderUsername, // toUser
                    message,
                    url
            );

            log.info("Sent delivery cancellation notification for order {} to sender {}",
                    order.getId(), order.getSenderId());
        } catch (Exception e) {
            log.error("Failed to send cancellation notification for order {}: {}",
                    order.getId(), e.getMessage(), e);
        }
    }

    /**
     * Sends a notification to the sender that their order has been returned to the hub
     */
    private void sendReturnedToHubNotification(Order order) {
        try {

            String senderUsername = order.getCreatedBy();
            String message = "Your order has been returned to our hub after unsuccessful delivery attempts. " +
                    "Please contact customer service to arrange for redelivery or pickup.";
            String url = "/order/view" + order.getId();

            notificationsService.create(
                    "SYSTEM", // fromUser
                    senderUsername, // toUser
                    message,
                    url
            );

            log.info("Sent returned-to-hub notification for order {} to sender {}",
                    order.getId(), order.getSenderId());
        } catch (Exception e) {
            log.error("Failed to send returned-to-hub notification for order {}: {}",
                    order.getId(), e.getMessage(), e);
        }
    }
    public List<TodayAssignmentShipperDto> getShipperAssignmentsTodayByHub(UUID hubId){
        // Check if hub exists
        if (!hubRepo.existsById(hubId)) {
            throw new NotFoundException("Hub not found with ID: " + hubId);
        }
        // Get today's date at start of day
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusSeconds(1);

        // Convert LocalDateTime to Timestamp
        Timestamp startTimestamp = Timestamp.valueOf(startOfDay);
        Timestamp endTimestamp = Timestamp.valueOf(endOfDay);
        // Get all shippers
        List<Shipper> shippers = shipperRepo.findAllByHubId(hubId);
        if(shippers.isEmpty()) return new ArrayList<>();
        // Get all assignments for the hub
        List<AssignOrderShipper> assignments = new ArrayList<>();
        for(Shipper shipper : shippers){
            List<AssignOrderShipper> assignOrderShippers = assignOrderShipperRepository
                    .findByShipperIdAndCreatedAtBetween(
                            shipper.getId(),
                            startTimestamp,
                            endTimestamp
                    );
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
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        // Chuyển đối tượng AssignOrderCollector thành TodayAssignmentDto
        List<AssignOrderShipper> assignments = assignOrderShipperRepository.findByShipperIdAndCreatedAtBetween(
                shipperId,
                startOfDay,
                endOfDay
        );
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
                .assignmentStatus(assignOrderShipper.getStatus())
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