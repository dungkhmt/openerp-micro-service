package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.CollectorAssignmentService;
import openerp.openerpresourceserver.service.NotificationsService;
import openerp.openerpresourceserver.service.context.DistributeContext;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static openerp.openerpresourceserver.utils.DistanceCalculator.HaversineDistanceCalculator.calculateDistance;

@Service
@Log4j2
@RequiredArgsConstructor
public class CollectorAssignmentServiceImpl implements CollectorAssignmentService {
    private final HubRepo hubRepo;
    private final OrderRepo orderRepo;
    private final SenderRepo senderRepo;
    private final CollectorRepo collectorRepo;
    private final AssignOrderCollectorRepository assignOrderCollectorRepository;
    private final DistributeContext distributeContext;
    private final NotificationsService notificationsService;


    @Override
    public List<OrderResponseCollectorShipperDto> suggestOrderToCollectorAssignment(
            Principal principal,
            UUID hubId,
            List<OrderRequestDto> orders,
            List<EmployeeDTO> collectors) {

        if (hubId == null || orders.isEmpty() || collectors.isEmpty()) {
            throw new RuntimeException("Invalid data for assignment suggestion");
        }

        // Tìm Hub theo hubId
        Hub hub = hubRepo.findById(hubId).orElseThrow(() ->
                new NotFoundException("Hub not found")
        );

        // Chuẩn bị dữ liệu
        List<Order> orderList = new ArrayList<>();
        List<Employee> collectorList = new ArrayList<>();

        // Tính khoảng cách cho từng đơn hàng
        for (OrderRequestDto orderRequest : orders) {
            Order order = orderRepo.findById(orderRequest.getId()).orElseThrow(() ->
                    new NotFoundException("Order not found")
            );
            Sender sender = senderRepo.findById(order.getSenderId()).orElseThrow(() ->
                    new NotFoundException("Sender not found"));

            Double distance = calculateDistance(hub.getLatitude(), hub.getLongitude(),
                    sender.getLatitude(), sender.getLongitude());
            order.setDistance(distance);
            orderList.add(order);
        }

        // Tạo danh sách collector
        for (EmployeeDTO collectorDto : collectors) {
            Collector collector = collectorRepo.findById(collectorDto.getId()).orElseThrow(() ->
                    new NotFoundException("Collector not found")
            );
            collectorList.add(collector);
        }

        // Sử dụng thuật toán phân công để tạo đề xuất (KHÔNG lưu vào DB)
        Map<UUID, List<Order>> orderCollectorMap = distributeContext.assignOrderToEmployees(
                hub, orderList, collectorList);

        // Tạo response với thông tin đề xuất
        List<OrderResponseCollectorShipperDto> suggestions = new ArrayList<>();
        for (Map.Entry<UUID, List<Order>> entry : orderCollectorMap.entrySet()) {
            UUID collectorId = entry.getKey();
            String collectorName = getCollectorNameById(collectorList, collectorId);
            int sequenceNumber = 1;

            for (Order order : entry.getValue()) {
                OrderResponseCollectorShipperDto suggestion = OrderResponseCollectorShipperDto.builder()
                        .id(order.getId())
                        .collectorId(collectorId)
                        .collectorName(collectorName)
                        .sequenceNumber(sequenceNumber++)
                        .senderName(order.getSenderName())
                        .recipientName(order.getRecipientName())
                        .status(OrderStatus.PENDING) // Vẫn giữ trạng thái pending
                        .build();
                suggestions.add(suggestion);
            }
        }

        return suggestions;
    }
    @Override
    @Transactional
    public List<OrderResponseCollectorShipperDto> confirmOrderToCollectorAssignment(
            Principal principal,
            UUID hubId,
            List<ConfirmAssignmentDto.AssignmentDetailDto> assignments) {

        if (assignments.isEmpty()) {
            throw new RuntimeException("No assignments to confirm");
        }

        List<AssignOrderCollector> assignOrderCollectors = new ArrayList<>();
        List<Order> ordersToUpdate = new ArrayList<>();
        List<OrderResponseCollectorShipperDto> responses = new ArrayList<>();

        for (ConfirmAssignmentDto.AssignmentDetailDto assignment : assignments) {
            // Tìm order
            Order order = orderRepo.findById(assignment.getOrderId())
                    .orElseThrow(() -> new NotFoundException("Order not found"));

            // Cập nhật trạng thái order
            order.setStatus(OrderStatus.ASSIGNED);
            order.setChangedBy(principal.getName());
            ordersToUpdate.add(order);

            // Tạo AssignOrderCollector
            AssignOrderCollector assignOrderCollector = new AssignOrderCollector();
            assignOrderCollector.setOrderId(assignment.getOrderId());
            assignOrderCollector.setCollectorId(assignment.getEmployeeId());
            assignOrderCollector.setCollectorName(assignment.getEmployeeName());
            assignOrderCollector.setSequenceNumber(assignment.getSequenceNumber());
            assignOrderCollector.setStatus(CollectorAssignmentStatus.ASSIGNED);
            assignOrderCollector.setCreatedBy(principal.getName());
            assignOrderCollectors.add(assignOrderCollector);

            // Tạo response
            OrderResponseCollectorShipperDto response = OrderResponseCollectorShipperDto.builder()
                    .id(order.getId())
                    .collectorId(assignment.getEmployeeId())
                    .collectorName(assignment.getEmployeeName())
                    .status(OrderStatus.ASSIGNED)
                    .build();
            responses.add(response);
        }

        // Lưu tất cả vào database
        orderRepo.saveAll(ordersToUpdate);
        assignOrderCollectorRepository.saveAll(assignOrderCollectors);

        return responses;
    }

    /**
     * Updates the status of a collector assignment and the related order if necessary.
     *
     * @param principal
     * @param assignmentId The ID of the assignment to update
     * @param status       The new status to set
     * @throws NotFoundException if the assignment is not found
     */
    @Override
    public void updateAssignmentStatus(Principal principal, UUID assignmentId, CollectorAssignmentStatus status) {
        AssignOrderCollector assignment = assignOrderCollectorRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));

        assignment.setStatus(status);
        Order order = orderRepo.findById(assignment.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));
        // If assignment is completed, update the order status accordingly
        if (status == CollectorAssignmentStatus.COMPLETED) {

            order.setStatus(OrderStatus.COLLECTED_COLLECTOR);
            order.setChangedBy(principal.getName());
        }
        else if (status == CollectorAssignmentStatus.FAILED_ONCE) {
            order.setCollectAttemptCount(order.getCollectAttemptCount() + 1);
            if(order.getCollectAttemptCount() >= 2) {
                order.setStatus(OrderStatus.CANCELLED);
                sendCancelledNotification(order, "Đơn hàng đã bị hủy do không thể thu gom sau 2 lần thử. ");
            }
            else{
                order.setStatus(OrderStatus.COLLECT_FAILED);
            }
            order.setChangedBy(principal.getName());
        }

        orderRepo.save(order);
        assignOrderCollectorRepository.save(assignment);
    }
    @Override
    public List<AssignOrderCollectorDTO> getAssignmentTodayByCollectorId(Principal principal,UUID collectorId) {
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        // Chuyển đối tượng AssignOrderCollector thành TodayAssignmentDto
        return assignOrderCollectorRepository.findByCollectorIdAndCreatedAtBetween1(
                collectorId,
                startOfDay,
                endOfDay
        );
    }
    @Override
    public List<TodayAssignmentDto> getAssignmentTodayByHubId(Principal principal, UUID hubId) {
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(today.plusDays(1).atStartOfDay());
        List<Collector> collectors = collectorRepo.findAllByHubId(hubId);

        List<AssignOrderCollector> assignments = new ArrayList<>();
        for(Collector collector : collectors){
            List<AssignOrderCollector> assignOrderShippers = assignOrderCollectorRepository
                    .findByCollectorIdAndCreatedAtBetween(
                            collector.getId(),
                            startOfDay,
                            endOfDay
                    );
            assignments.addAll(assignOrderShippers);
        }


        // Chuyển đối tượng AssignOrderCollector thành TodayAssignmentDto
        return assignments.stream()
                .collect(Collectors.toMap(
                        AssignOrderCollector::getCollectorId, // Group by collectorId
                        assignment -> TodayAssignmentDto.builder()
                                .collectorId(assignment.getCollectorId()) // ID của collector
                                .collectorName(assignment.getCollectorName()) // Tên collector
                                .numOfOrders(countOrdersForCollector(assignments, assignment.getCollectorId())) // Số đơn hàng
                                .numOfCompleted(assignments.stream()
                                        .filter(a -> a.getCollectorId().equals(assignment.getCollectorId()))
                                        .filter(a -> "COMPLETED".equals(a.getStatus().toString()))
                                        .count())
                                .status(assignment.getStatus().toString()) // Trạng thái
                                .build(),
                        (existing, replacement) -> existing) // Nếu có trùng lặp, giữ lại bản ghi đầu tiên
                ).values().stream() // Lấy danh sách các giá trị (TodayAssignmentDto)
                .sorted(Comparator.comparing(TodayAssignmentDto::getCollectorName)) // Sắp xếp theo collectorId
                .collect(Collectors.toList());
    }
    private String getCollectorNameById(List<Employee> employees, UUID collectorId) {
        return employees.stream()
                .filter(collector -> collector.getId().equals(collectorId))
                .map(Employee::getName)
                .findFirst()
                .orElse(null); // hoặc trả về giá trị mặc định
    }
    private void sendCancelledNotification(Order order, String reason) {
        try {

            String senderUsername = order.getCreatedBy();
            String message = "Rất tiếc, đơn hàng của bạn đã bị hủy. " + reason +
                    "Hãy liên lạc với chúng tôi để biết thêm chi tiết.";
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
    // Phương thức đếm số đơn hàng cho collectorId trong danh sách assignments
    private Long countOrdersForCollector(List<AssignOrderCollector> assignments, UUID collectorId) {
        return assignments.stream()
                .filter(assignment -> assignment.getCollectorId().equals(collectorId)) // Lọc các assignment theo collectorId
                .count(); // Đếm số lượng
    }
}
