package openerp.openerpresourceserver.service.impl;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.cache.RedisCacheService;
import openerp.openerpresourceserver.context.DistributeContext;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.*;
import openerp.openerpresourceserver.mapper.OrderMapper;
import openerp.openerpresourceserver.repository.*;
import openerp.openerpresourceserver.service.AssignmentService;
import openerp.openerpresourceserver.service.OrderService;
import openerp.openerpresourceserver.utils.GAAutoAssign.GAAutoAssign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;

import static openerp.openerpresourceserver.utils.DistanceCalculator.HaversineDistanceCalculator.calculateDistance;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private DistributeContext distributeContext;
    @Autowired
    private GAAutoAssign gaAutoAssign;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private VehicleRepository vehicleRepo;

    private final OrderMapper orderMapper = OrderMapper.INSTANCE;
    @Autowired
    private AssignOrderShipperRepository assignOrderShipperRepository;
    @Autowired
    private AssignOrderCollectorRepository assignOrderCollectorRepository;
    @Autowired
    private OrderItemRepo orderItemRepo;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SenderRepo senderRepo;
    @Autowired
    private TripOrderRepository tripOrderRepository;
    @Autowired
    private RecipientRepo recipientRepo;
    @Autowired
    private HubRepo hubRepo;
    @Autowired
    private CollectorRepo collectorRepo;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private RouteStopRepository routeStopRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleDriverRepository vehicleDriverRepository;
    @Autowired
    private RouteScheduleRepository routeScheduleRepository;
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RedisCacheService redisCacheService;

    // Create order method
    @Override
    @Transactional
    public Order createOrder(Principal principal, OrderRequestDto orderREQ) {
        Order orderEntity = new Order();
        Sender sender = senderRepo.findByNameAndPhone(orderREQ.getSenderName(), orderREQ.getSenderPhone());
        Recipient recipient = recipientRepo.findByNameAndPhone(orderREQ.getRecipientName(), orderREQ.getRecipientPhone());


        if (sender == null) {
            sender = new Sender(orderREQ.getSenderName(), orderREQ.getSenderPhone(), orderREQ.getSenderEmail(), orderREQ.getSenderAddress());
            sender.setLatitude(orderREQ.getSenderLatitude());
            sender.setLongitude(orderREQ.getSenderLongitude());
            sender = senderRepo.save(sender);

        }

        if (recipient == null) {
            recipient = new Recipient(orderREQ.getRecipientName(), orderREQ.getRecipientPhone(), orderREQ.getRecipientEmail(), orderREQ.getRecipientAddress());
            recipient.setLatitude(orderREQ.getRecipientLatitude());
            recipient.setLongitude(orderREQ.getRecipientLongitude());
            recipient = recipientRepo.save(recipient);
        }

        orderEntity.setStatus(OrderStatus.PENDING);
        orderEntity.setTotalPrice(orderREQ.getTotalPrice());
        orderEntity.setShippingPrice(orderREQ.getShippingPrice());
        orderEntity.setFinalPrice(orderREQ.getFinalPrice());
        orderEntity.setSenderId(sender.getSenderId());
        orderEntity.setRecipientId(recipient.getRecipientId());
        orderEntity.setOrderType(orderREQ.getOrderType());
        orderEntity.setOrderType(orderREQ.getOrderType());
        orderEntity.setCreatedBy(principal.getName());
        orderEntity.setSenderName(orderREQ.getSenderName());
        orderEntity.setRecipientName(orderREQ.getRecipientName());
        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItem> items = orderREQ.getItems();

        for (OrderItem item : items) {
            OrderItem orderItem = OrderItem.builder()
                    .name(item.getName())
                    .quantity(item.getQuantity())
                    .weight(item.getWeight())
                    .price(item.getPrice())

                    .createdBy(principal.getName())
                    .orderId(orderEntity.getId())
                    .build();
            orderItems.add(orderItem);
        }

        orderItemRepo.saveAll(orderItems);

        orderEntity.setCreatedBy(principal.getName());
        logger.info("Created Order: {}", orderEntity);

        assignmentService.assignOrderToHub(orderEntity);
        Order savedOrder = orderRepo.save(orderEntity);

        return savedOrder;
    }

    // Get all orders method
    @Override
    public List<OrderSummaryDTO> getAllOrders() {
        List<Order> orderList = getAllOrdersInCacheElseDatabase();
        List<OrderSummaryDTO> orderSummaries = new ArrayList<>();
        for (Order order : orderList) {
            orderSummaries.add(new OrderSummaryDTO(order));
        }
        return orderSummaries;
    }

    public List<Order> getAllOrdersInCacheElseDatabase() {
        List<Order> orderList = redisCacheService.getCachedListObject(RedisCacheService.ALL_ORDER_KEY, Order.class);

        if (orderList == null) {
            orderList = orderRepo.findAll();
            redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_ORDER_KEY, orderList, RedisCacheService.DEFAULT_EXPIRE_TIME_IN_MINUTES);
            return orderList;

        }
        // Nếu không có trong cache, lấy từ database và lưu vào cache
        return orderList;
    }

    @Override
    public List<OrderSummaryDTO> getAllOrdersByHubIdToday(UUID hubId) {
        List<OrderSummaryDTO> orderList = orderRepo.findOrdersCreatedToday(hubId);

        return orderList;
    }



    @Override
    public List<OrderSummaryDTO> getAllOrdersByHubId(UUID hubId) {
        List<Order> orderList = orderRepo.findByOriginHubIdOrderByCreatedAtDesc(hubId);
        List<OrderSummaryDTO> orderSummaries = new ArrayList<>();
        for (Order order : orderList) {
            // Thêm vào danh sách orderResponses
            orderSummaries.add(new OrderSummaryDTO(order));

        }
        return orderSummaries;
    }

    // Get order by ID method
    @Override
    public OrderResponseDto getOrderById(UUID orderId) {
        return orderRepo.findOrderDetailById(orderId);
    }

    // Get order by ID method
    @Override
    public List<OrderSummaryDTO> getOrderByUsername(String username) {

        List<Order> orders = orderRepo.findByCreatedBy(username);
        if(orders.isEmpty()){
            return Collections.emptyList();
        }
        else {
            List<OrderSummaryDTO> orderSummaries = new ArrayList<>();
            for (Order order : orders) {
                // Thêm vào danh sách orderResponses
                orderSummaries.add(new OrderSummaryDTO(order));
            }
            return orderSummaries.stream().sorted(Comparator.comparing(OrderSummaryDTO::getCreatedAt)).collect(Collectors.toList());
        }
    }

    // Edit order method
    @Override
    @Transactional
    public Order editOrder(Principal principal, UUID orderId, OrderRequestDto orderREQ) {
        try {
            // Lấy người dùng hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Set<Role> userRoles = authentication.getAuthorities().stream()
                    .map(authority -> {
                        try {
                            return Role.valueOf(authority.getAuthority().replace("ROLE_", ""));
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException("Invalid role: " + authority.getAuthority(), e);
                        }
                    })
                    .collect(Collectors.toSet());

            // Tìm đơn hàng theo ID
            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Order not found"));

            OrderStatus currentStatus = order.getStatus();
            OrderStatus newStatus = orderREQ.getStatus();

            // Kiểm tra trạng thái chuyển đổi có hợp lệ không
            if (!OrderStatus.isValidTransition(currentStatus, newStatus)) {
                throw new NotFoundException("Cannot transition from " + currentStatus + " to " + newStatus);
            }

            // Kiểm tra quyền cập nhật
            boolean hasPermission = false;
            for (Role role : userRoles) {
                if (newStatus.getRolesAllowedToUpdate().contains(role)) {
                    hasPermission = true;
                    break;
                }
            }

            if (!hasPermission) {
                throw new RuntimeException("User does not have permission to update order to status: " + newStatus);
            }

            // Tìm đơn hàng hiện tại
            Order existingOrder = orderRepo.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

            // Tìm hoặc tạo mới người gửi
            Sender sender = senderRepo.findByNameAndPhone(orderREQ.getSenderName(), orderREQ.getSenderPhone());
            if (sender == null) {
                sender = new Sender(orderREQ.getSenderName(), orderREQ.getSenderPhone(), orderREQ.getSenderEmail(), orderREQ.getSenderAddress());
            }

            // Tìm hoặc tạo mới người nhận
            Recipient recipient = recipientRepo.findByNameAndPhone(orderREQ.getRecipientName(), orderREQ.getRecipientPhone());
            if (recipient == null) {
                recipient = new Recipient(orderREQ.getRecipientName(), orderREQ.getRecipientPhone(), orderREQ.getRecipientEmail(), orderREQ.getRecipientAddress());
            }

            // Cập nhật thông tin đơn hàng
            existingOrder.setSenderId(sender.getSenderId());
            existingOrder.setSenderName(sender.getName());
            existingOrder.setRecipientId(recipient.getRecipientId());
            existingOrder.setRecipientName(recipient.getName());
            existingOrder.setTotalPrice(orderREQ.getTotalPrice());
            existingOrder.setShippingPrice(orderREQ.getShippingPrice());
            existingOrder.setFinalPrice(orderREQ.getFinalPrice());
            existingOrder.setOrderType(orderREQ.getOrderType());
            existingOrder.setStatus(orderREQ.getStatus() != null ? orderREQ.getStatus() : OrderStatus.PENDING);
            existingOrder.setChangedBy(principal.getName());
            // Xử lý danh sách các mục đơn hàng
            List<OrderItem> orderItems = new ArrayList<>();
            if (orderREQ.getItems() != null) {
                orderItems = orderREQ.getItems().stream()
                        .map(item -> OrderItem.builder()
                                .name(item.getName())
                                .quantity(item.getQuantity())
                                .weight(item.getWeight())
                                .price(item.getPrice())
                                .updatedBy(principal.getName())
                                .build()
                        )
                        .peek(item -> item.setOrderId(existingOrder.getId()))
                        .collect(Collectors.toList());
            }

            // Lưu các mục đơn hàng
            orderItemRepo.saveAll(orderItems);

            // Lưu và trả về đơn hàng đã cập nhật
            return orderRepo.save(existingOrder);

        } catch (NotFoundException e) {
            // Xử lý khi không tìm thấy đơn hàng
            throw e; // Re-throw để xử lý ở lớp cao hơn
        } catch (RuntimeException e) {
            // Xử lý các ngoại lệ runtime khác
            throw new RuntimeException("Failed to update order: " + e.getMessage(), e);
        } catch (Exception e) {
            // Xử lý các ngoại lệ không mong đợi khác
            throw new RuntimeException("An unexpected error occurred while updating order: " + e.getMessage(), e);
        }
    }

    // Delete order method
    @Override
    @Transactional
    public void deleteOrder(Principal principal, UUID orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

//        // Store vehicle ID for load update if order is assigned to a vehicle
//        UUID vehicleId = order.getVehicleId();
//        OrderHistory orderHistory = OrderHistory.builder()
//                .orderId(order.getId())
//                        .
        order.setStatus(OrderStatus.DELETED);
        // Delete order items
        List<OrderItem> orderItems = orderItemRepo.findAllByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            orderItem.setStatus(OrderItemStatus.DELETED);
            orderItemRepo.save(orderItem);
        }

        orderItemRepo.deleteAllByOrderId(orderId);

        // Delete the order
        orderRepo.deleteById(orderId);
//
//        // Update vehicle load if order was assigned to a vehicle
//        if (vehicleId != null && order.getStatus() == OrderStatus.DELIVERING) {
//            assignmentService.decreaseVehicleLoad(vehicleId, orderId);
//        }

        log.info("Deleted Order with ID: {}", orderId);
    }

    @Override
    public List<OrderResponseCollectorShipperDto> autoAssignOrderToCollector(UUID hubId, List<OrderRequestDto> orders, List<EmployeeDTO> collectors) {
        if (hubId == null || orders.isEmpty() || collectors.isEmpty()) {
            throw new RuntimeException("Not valid data");
        }

        // Tìm Hub theo hubId
        Hub hub = hubRepo.findById(hubId).orElseThrow(() ->
                new NotFoundException("Not found hub")
        );

        // Lấy tọa độ của hub
        Double hubLat = hub.getLatitude();
        Double hubLon = hub.getLongitude();

        // Danh sách đơn hàng và collector
        List<Order> orderList = new ArrayList<>();
        List<Employee> collectorList = new ArrayList<>();
        List<OrderResponseDto> assignedOrders = new ArrayList<>();

        // Tính khoảng cách cho từng đơn hàng
        for (OrderRequestDto orderRequest : orders) {
            Order order = orderRepo.findById(orderRequest.getId()).orElseThrow(() ->
                    new NotFoundException("Not found order")
            );
            Sender sender = senderRepo.findById(order.getSenderId()).orElseThrow(() -> new NotFoundException("not found sender"));

            Double distance = calculateDistance(hubLat, hubLon, sender.getLatitude(), sender.getLongitude());
            order.setDistance(distance);
            orderRepo.save(order);
            orderList.add(order);
        }

        // Tạo danh sách collector từ DTO
        for (EmployeeDTO collectorDto : collectors) {
            Collector collector = collectorRepo.findById(collectorDto.getId()).orElseThrow(() ->
                    new NotFoundException("Not found collector")
            );
            collectorList.add(collector);
        }
//
//        // Thuật toán tham lam: phân công đơn hàng cho collector gần nhất
//        for (Order order : orderList) {
//            Collector closestCollector = null;
//            Double minDistance = Double.MAX_VALUE;
//
//            // Tìm collector gần nhất với đơn hàng
//            for (Collector collector : collectorList) {
//                Double distanceToCollector = calculateDistance(
//                        hub.getLatitude(),
//                        hub.getLongitude(),
//                        order.getSender().getLatitude(),
//                        order.getSender().getLongitude()
//                );
//
//                // Nếu collector này gần hơn, cập nhật closestCollector
//                if (distanceToCollector < minDistance) {
//                    minDistance = distanceToCollector;
//                    closestCollector = collector;
//                }
//            }
//
//            // Nếu tìm được collector gần nhất, phân công đơn hàng
//            if (closestCollector != null) {
//                // Cập nhật đơn hàng với collector đã phân công
//                OrderResponse orderResponse = new OrderResponse();
//                orderResponse.setId(order.getId());
//                orderResponse.setCollector(closestCollector);
//                orderResponse.setCollectorName(closestCollector.getName());
//                assignedOrders.add(orderResponse);
//
//                // Cập nhật trạng thái hoặc logic khác sau khi phân công
//                // Có thể cập nhật đơn hàng là đã được phân công cho collector
//                order.setCollector(closestCollector);
//
//                // Loại bỏ collector đã phân công (hoặc có thể cập nhật trạng thái nếu cần)
//                collectorList.remove(closestCollector);
//            }
//        }
//
//        return assignedOrders;
        Map<UUID, List<Order>> orderCollectorMap = distributeContext.assignOrderToEmployees(hub, orderList, collectorList);
        List<AssignOrderCollector> assignments = new ArrayList<>();
        for (Map.Entry<UUID, List<Order>> entry : orderCollectorMap.entrySet()) {
            UUID collectorId = entry.getKey();
            int sequenceNumber = 1;
            for (Order order : entry.getValue()) {
                // Lưu kết quả vào bảng AssignOrderCollector
                AssignOrderCollector assignment = new AssignOrderCollector();
                assignment.setOrderId(order.getId());
                assignment.setSequenceNumber(sequenceNumber++);
                assignment.setCollectorId(collectorId);
                assignment.setCollectorName(getCollectorNameById(collectorList, collectorId));
                // Set các trường khác nếu cần (như createdBy, approvedBy, v.v.)
                assignment.setCreatedBy("admin"); // Example, bạn có thể thay đổi
                assignment.setStatus(CollectorAssignmentStatus.ASSIGNED);
                // Lưu vào database
                assignments.add(assignment);
            }
        }
         //Lưu tất cả AssignOrderCollector vào cơ sở dữ liệu một lần
            this.assignOrderCollectorRepository.saveAll(assignments);
        List<OrderResponseCollectorShipperDto> responses = new ArrayList<>();

        for (Map.Entry<UUID, List<Order>> entry : orderCollectorMap.entrySet()) {
            UUID collectorId = entry.getKey();
            for (Order order : entry.getValue()) {
                responses.add(new OrderResponseCollectorShipperDto().builder()
                        .id(order.getId())
                        .collectorId(collectorId)
                        .build());
            }
        }

        return responses;
    }

    private String getCollectorNameById(List<Employee> employees, UUID collectorId) {
        return employees.stream()
                .filter(collector -> collector.getId().equals(collectorId))
                .map(Employee::getName)
                .findFirst()
                .orElse(null); // hoặc trả về giá trị mặc định
    }
    // Phương thức để lấy các bản ghi trong ngày hôm nay theo hubId
    public List<TodayAssignmentDto> getAssignmentTodayByHubId(UUID hubId) {
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

    // Phương thức đếm số đơn hàng cho collectorId trong danh sách assignments
    private Long countOrdersForCollector(List<AssignOrderCollector> assignments, UUID collectorId) {
        return assignments.stream()
                .filter(assignment -> assignment.getCollectorId().equals(collectorId)) // Lọc các assignment theo collectorId
                .count(); // Đếm số lượng
    }


    public List<AssignOrderCollectorDTO> getAssignmentTodayByCollectorId(UUID collectorId) {
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
    @Transactional
    public boolean confirmCollectedHub(Principal principal, UUID[] orderIds) {
        List<UUID> orderIdList = Arrays.asList(orderIds);
        if (orderIdList.isEmpty()) {
            log.info("Order id list to update complete is empty");
            return false;
        }
        List<Order> updatedOrder = new ArrayList<>();
        List<AssignOrderCollector> updatedAssignments = new ArrayList<>();

        for (UUID orderId : orderIdList) {
            // Fetch the order and check it exists
            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("order not found: " + orderId));

            // Check if status is not null before comparison
            if (order.getStatus() == null) {
                log.error("Order {} has null status", orderId);
                throw new RuntimeException("Order has null status");
            }

            if (order.getStatus() != OrderStatus.COLLECTED_COLLECTOR) {
                throw new RuntimeException("Đơn hàng chưa được collector collect");
            }

            // Make sure no null fields before setting new values
            order.setStatus(OrderStatus.COLLECTED_HUB);

            // Set the changed by field safely
            order.setChangedBy(principal != null ? principal.getName() : "system");
            updatedOrder.add(order);

            // Fetch assignment and check for null
            AssignOrderCollector assignOrderCollector = assignOrderCollectorRepository
                    .findByOrderIdAndStatus(orderId, CollectorAssignmentStatus.COLLECTED);

            if (assignOrderCollector != null) {
                assignOrderCollector.setStatus(CollectorAssignmentStatus.COMPLETED);
                updatedAssignments.add(assignOrderCollector);
            }
        }

        // Save all updated entities
        if (!updatedOrder.isEmpty()) {
            orderRepo.saveAll(updatedOrder);
        }

        if (!updatedAssignments.isEmpty()) {
            assignOrderCollectorRepository.saveAll(updatedAssignments);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean confirmOutHub(Principal principal,UUID[] orderIds, UUID vehicleId) {
        List<UUID> orderIdList = Arrays.asList(orderIds);
        if (orderIdList.isEmpty()) {
            log.info("Order id list to update complete is empty");
            return false;
        }
        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found " + vehicleId));
        List<Order> updatedOrder = new ArrayList<>();
        for (UUID orderId : orderIds) {
            Order order = orderRepo.findById(orderId).orElseThrow(() -> new NotFoundException("order not found " + orderId));
            order.setStatus(OrderStatus.DELIVERING);
//            order.setVehicleId(vehicleId);
//            order.setVehicleType(vehicle.getVehicleType());
//            order.setVehicleLicensePlate(vehicle.getPlateNumber());
            order.setChangedBy(principal.getName());

            updatedOrder.add(order);
        }
        orderRepo.saveAll(updatedOrder);
        return true;
    }

    @Override
    public List<OrderSummaryDTO> getCollectedCollectorList(UUID hubId) {
        return orderRepo.getCollectedColelctorList(hubId);
    }

    @Override
    public List<OrderSummaryDTO> getCollectedHubList(UUID hubId) {
        return orderRepo.getCollectedHubList(hubId);
    }

// Add this to the OrderServiceImpl class

    @Transactional
    public boolean markOrderDelivered(UUID orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        // Check if order is in a valid state
        if (order.getStatus() != OrderStatus.DELIVERING) {
            log.warn("Order {} is not in DELIVERING state, current status: {}", orderId, order.getStatus());
            return false;
        }

//        // Store vehicle ID for load update
//        UUID vehicleId = order.getVehicleId();

        // Update order status
        order.setStatus(OrderStatus.COMPLETED);
        orderRepo.save(order);

//        // Decrease vehicle load
//        if (vehicleId != null) {
//            assignmentService.decreaseVehicleLoad(vehicleId, orderId);
//        }

        return true;
    }


    /**
     * Get optimal orders for a specific vehicle's route at a hub
     *
     * @return List of optimal orders that match the route, sorted by age
     */
//    @Override
//    public List<OrderItemForTripDto> getOrderItemsForTrip(UUID tripId) {
//
//        List<TripItem> tripItems = tripItemRepository.findByTripId(tripId);
//        if (tripItems.isEmpty()) {
//            log.warn("No order item found for this trip");
//            return Collections.emptyList();
//        }
//
//        List<OrderItem> orderItems = tripItems.stream().map(tripItem -> orderItemRepo.findById(tripItem.getOrderItemId()).orElse(null)).collect(Collectors.toList());
//
//        List<OrderItemForTripDto> orderItemForTripDtos = orderItems.stream().map(o -> {
//            OrderItemForTripDto orderItemForTripDto = new OrderItemForTripDto(o);
//            Order order = orderRepo.findById(o.getOrderId()).orElseThrow(() -> new NotFoundException("order not found " + o.getOrderId()));
//            Sender sender = senderRepo.findById(order.getSenderId()).orElseThrow(() -> new NotFoundException("sender not found " + order.getSenderId()));
//            Recipient recipient = recipientRepo.findById(order.getRecipientId()).orElseThrow(() -> new NotFoundException("sender not found " + order.getSenderId()));
//            orderItemForTripDto.setOrderId(order.getId());
//            orderItemForTripDto.setSenderName(order.getSenderName());
//            orderItemForTripDto.setRecipientName(order.getRecipientName());
//            orderItemForTripDto.setSenderPhone(sender.getPhone());
//            orderItemForTripDto.setRecipientPhone(recipient.getPhone());
//            orderItemForTripDto.setSenderAddress(sender.getAddress());
//            orderItemForTripDto.setRecipientAddress(recipient.getAddress());
//            return orderItemForTripDto;
//        }).collect(Collectors.toList());
//
//
//        return orderItemForTripDtos;
//    }


    public Double getOrderWeight(UUID orderId) {
        List<OrderItem> orderItems = orderItemRepo.findAllByOrderId(orderId);
        return orderItems.stream().map(OrderItem::getWeight).reduce(0.0, Double::sum);
    }

    public Double getOrderVolume(UUID orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new NotFoundException("order not found " + orderId));
        return order.getWidth() * order.getHeight() * order.getLength() / 1000000;
    }

    public OrderItemForTripDto convertOrderItemToDtoForTrip(OrderItem orderItem) {
        OrderItemForTripDto orderItemForTripDto = new OrderItemForTripDto(orderItem);
        Order order = orderRepo.findById(orderItem.getOrderId()).orElseThrow(() -> new NotFoundException("order not found " + orderItem.getOrderId()));
        Sender sender = senderRepo.findById(order.getSenderId()).orElseThrow(() -> new NotFoundException("sender not found " + order.getSenderId()));
        Recipient recipient = recipientRepo.findById(order.getRecipientId()).orElseThrow(() -> new NotFoundException("sender not found " + order.getSenderId()));
        orderItemForTripDto.setOrderId(order.getId());
        orderItemForTripDto.setSenderName(order.getSenderName());
        orderItemForTripDto.setRecipientName(order.getRecipientName());
        orderItemForTripDto.setSenderPhone(sender.getPhone());
        orderItemForTripDto.setRecipientPhone(recipient.getPhone());
        orderItemForTripDto.setSenderAddress(sender.getAddress());
        orderItemForTripDto.setRecipientAddress(recipient.getAddress());
        return orderItemForTripDto;
    }

    public List<OrderSummaryDTO> getAllOrdersDeliveredInHub(UUID hubId){
        List<Order> orderList = orderRepo.findAllByFinalHubIdAndStatus(hubId, OrderStatus.CONFIRMED_IN_FINAL_HUB);
        List<OrderSummaryDTO> orderSummaries = new ArrayList<>();
        for (Order order : orderList) {
            // Thêm vào danh sách orderResponses
            orderSummaries.add(new OrderSummaryDTO(order));

        }
        return orderSummaries;
    }

    @Override
    public List<Order> getCustomerOrders(UUID customerId) {
        return orderRepo.findBySenderId(customerId);
    }
    @Override
    public List<OrderSummaryDTO> getCollectedCollectorOrders(UUID hubId) {
        // Get orders with status COLLECTED_COLLECTOR at this hub
        return orderRepo.findByOriginHubIdAndStatus(hubId, OrderStatus.COLLECTED_COLLECTOR)
                .stream()
                .map(o -> new OrderSummaryDTO((Order) o))
                .collect(Collectors.toList());
    }

    @Override
    public List<TripOrderSummaryDto> getDeliveredDriverOrders(UUID hubId) {
        // Get orders with status DELIVERED_HUB at this hub
        return orderRepo.findByFinalHubIdAndStatus(hubId, OrderStatus.DELIVERED)
                .stream()
                .map(o -> {
                    TripOrder tripOrder =  tripOrderRepository.findTopByOrderIdOrderByCreatedAtDesc(o.getId());
                    Trip trip = tripRepository.findById(tripOrder.getTripId()).orElseThrow(() -> new NotFoundException("trip not found " + tripOrder.getTripId()));
                    Vehicle vehicle = vehicleRepository.findById(trip.getVehicleId()).orElseThrow(() -> new NotFoundException("vehicle not found " + trip.getVehicleId()));
                    return new TripOrderSummaryDto((Order) o, trip,vehicle);})
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderSummaryDTO> getFailedDeliveryOrders(UUID hubId) {
        // Get orders with status DELIVERY_FAILED at this hub
        return orderRepo.findByOriginHubIdAndStatus(hubId, OrderStatus.DELIVERED_FAILED)
                .stream()
                .map(o -> new OrderSummaryDTO((Order) o))
                .collect(Collectors.toList());
    }

    @Override
    public boolean confirmOrdersIntoHub(Principal principal, UUID[] orderIds) {
        try {
            for (UUID orderId : orderIds) {
                Order order = orderRepo.findById(orderId)
                        .orElseThrow(() -> new RuntimeException("Order not found"));

                // Update status based on current status
                switch (order.getStatus()) {
                    case COLLECTED_COLLECTOR:
                        order.setStatus(OrderStatus.COLLECTED_HUB);
                        break;
                    case DELIVERING:
                        order.setStatus(OrderStatus.DELIVERED);
                        break;
                    case DELIVERED:
                        order.setStatus(OrderStatus.CONFIRMED_IN_FINAL_HUB);
                        break;
                    case DELIVERED_FAILED:
                        order.setStatus(OrderStatus.RETURNED_HUB_AFTER_DELIVERED);
                        break;
                    case SHIPPED_FAILED:
                        order.setStatus(OrderStatus.RETURNED_HUB_AFTER_SHIP_FAIL);
                        break;
                                       default:
                        // Maintain current status or set default
                        break;
                }
                order.setChangedBy(principal.getName());
                TripOrder tripOrder = tripOrderRepository.findTopByOrderIdOrderByCreatedAtDesc(order.getId());
                if (tripOrder != null) {
                    tripOrder.setStatus("COMPLETED");
                    tripOrderRepository.save(tripOrder);
                }
                Trip trip = tripRepository.findById(tripOrder.getTripId()).orElseThrow(() -> new NotFoundException("trip not found " + tripOrder.getTripId()));
                trip.setStatus(TripStatus.CONFIRMED_IN);

                tripRepository.save(trip);
                tripOrderRepository.save(tripOrder);
                orderRepo.save(order);


            }
            return true;
        } catch (Exception e) {
            log.error("Error confirming orders into hub", e);
            return false;
        }
    }

    @Override
    public boolean confirmShipperPickup(Principal principal, UUID shipperId) {
        try {
            // Find all assigned orders for this shipper that haven't been picked up
            List<AssignOrderShipper> assignments = assignOrderShipperRepository
                    .findByShipperIdAndStatus(shipperId, ShipperAssignmentStatus.ASSIGNED);

            for (AssignOrderShipper assignment : assignments) {
                assignment.setStatus(ShipperAssignmentStatus.PICKED_UP);
                assignOrderShipperRepository.save(assignment);

                // Update order status
                UUID orderId = assignment.getOrderId();
                Order order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
                order.setStatus(OrderStatus.SHIPPING);
                order.setChangedBy(principal.getName());
                orderRepo.save(order);


            }
            return true;
        } catch (Exception e) {
            log.error("Error confirming shipper pickup", e);
            return false;
        }
    }

    @Override
    public boolean confirmMultipleShipperPickups(Principal principal, UUID[] shipperIds) {
        try {
            for (UUID shipperId : shipperIds) {
                confirmShipperPickup(principal, shipperId);
            }
            return true;
        } catch (Exception e) {
            log.error("Error confirming multiple shipper pickups", e);
            return false;
        }
    }

    @Override
    public List<TodayAssignmentShipperDto> getShipperPickupRequests(UUID hubId) {
        // Get shippers with assigned orders that haven't been picked up yet
        LocalDate today = LocalDate.now();

        return assignOrderShipperRepository.getShipperAssignmentsTodayByHub(hubId, today)
                .stream()
                .filter(assignment -> assignment.getNumOfOrders() > assignment.getNumOfCompleted())
                .collect(Collectors.toList());
    }
    @Override
    public List<CollectorOrderHistoryDto> getCollectorOrderHistory(UUID collectorId, LocalDate startDate, LocalDate endDate) {
        // Set default date range if not provided (last 30 days)
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // Convert dates to timestamps
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        // Get assignments within date range
        List<AssignOrderCollector> assignments = assignOrderCollectorRepository.findAll().stream()
                .filter(assignment -> assignment.getCollectorId().equals(collectorId) &&
                        assignment.getCreatedAt() != null &&
                        assignment.getCreatedAt().after(startTimestamp) &&
                        assignment.getCreatedAt().before(endTimestamp))
                .sorted(Comparator.comparing(AssignOrderCollector::getCreatedAt).reversed())
                .collect(Collectors.toList());

        // Map to DTOs with order details
        return assignments.stream()
                .map(this::mapToCollectorOrderHistoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShipperOrderHistoryDto> getShipperOrderHistory(UUID shipperId, LocalDate startDate, LocalDate endDate) {
        // Set default date range if not provided (last 30 days)
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // Convert dates to timestamps
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        // Get assignments within date range
        List<AssignOrderShipper> assignments = assignOrderShipperRepository.findByShipperId(shipperId).stream()
                .filter(assignment -> assignment.getCreatedAt() != null &&
                        assignment.getCreatedAt().after(startTimestamp) &&
                        assignment.getCreatedAt().before(endTimestamp))
                .sorted(Comparator.comparing(AssignOrderShipper::getCreatedAt).reversed())
                .collect(Collectors.toList());

        // Map to DTOs with order details
        return assignments.stream()
                .map(this::mapToShipperOrderHistoryDto)
                .collect(Collectors.toList());
    }

    private CollectorOrderHistoryDto mapToCollectorOrderHistoryDto(AssignOrderCollector assignment) {
        try {
            Order order = orderRepo.findById(assignment.getOrderId())
                    .orElseThrow(() -> new NotFoundException("Order not found: " + assignment.getOrderId()));

            Sender sender = senderRepo.findById(order.getSenderId())
                    .orElseThrow(() -> new NotFoundException("Sender not found: " + order.getSenderId()));

            Recipient recipient = recipientRepo.findById(order.getRecipientId())
                    .orElseThrow(() -> new NotFoundException("Recipient not found: " + order.getRecipientId()));

            // Count order items
            List<OrderItem> orderItems = orderItemRepo.findAllByOrderId(order.getId());
            int numOfItems = orderItems.size();
            double totalWeight = orderItems.stream().mapToDouble(OrderItem::getWeight).sum();

            return CollectorOrderHistoryDto.builder()
                    .assignmentId(assignment.getId())
                    .orderId(assignment.getOrderId())
                    .collectorId(assignment.getCollectorId())
                    .collectorName(assignment.getCollectorName())
                    .sequenceNumber(assignment.getSequenceNumber())
                    .assignmentStatus(assignment.getStatus())
                    .orderStatus(order.getStatus())
                    .senderName(sender.getName())
                    .senderPhone(sender.getPhone())
                    .senderAddress(sender.getAddress())
                    .senderLatitude(sender.getLatitude())
                    .senderLongitude(sender.getLongitude())
                    .recipientName(recipient.getName())
                    .recipientPhone(recipient.getPhone())
                    .recipientAddress(recipient.getAddress())
                    .assignmentCreatedAt(assignment.getCreatedAt())
                    .assignmentUpdatedAt(assignment.getUpdatedAt())
                    .orderCreatedAt(order.getCreatedAt())
                    .numOfItems(numOfItems)
                    .totalWeight(totalWeight)
                    .notes(assignment.getNotes())
                    .doneAt(assignment.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping assignment to collector order history DTO: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing assignment data", e);
        }
    }

    private ShipperOrderHistoryDto mapToShipperOrderHistoryDto(AssignOrderShipper assignment) {
        try {
            Order order = orderRepo.findById(assignment.getOrderId())
                    .orElseThrow(() -> new NotFoundException("Order not found: " + assignment.getOrderId()));

            Sender sender = senderRepo.findById(order.getSenderId())
                    .orElseThrow(() -> new NotFoundException("Sender not found: " + order.getSenderId()));

            Recipient recipient = recipientRepo.findById(order.getRecipientId())
                    .orElseThrow(() -> new NotFoundException("Recipient not found: " + order.getRecipientId()));

            return ShipperOrderHistoryDto.builder()
                    .assignmentId(assignment.getId())
                    .orderId(assignment.getOrderId())
                    .shipperId(assignment.getShipperId())
                    .shipperName(assignment.getShipperName())
                    .sequenceNumber(assignment.getSequenceNumber())
                    .assignmentStatus(assignment.getStatus())
                    .orderStatus(order.getStatus())
                    .senderName(sender.getName())
                    .senderPhone(sender.getPhone())
                    .senderAddress(sender.getAddress())
                    .recipientName(recipient.getName())
                    .recipientPhone(recipient.getPhone())
                    .recipientAddress(recipient.getAddress())
                    .recipientLatitude(recipient.getLatitude())
                    .recipientLongitude(recipient.getLongitude())
                    .assignmentCreatedAt(assignment.getCreatedAt())
                    .assignmentUpdatedAt(assignment.getUpdatedAt())
                    .orderCreatedAt(order.getCreatedAt())
                    .deliveryAttempts(assignment.getDeliveryAttempts())
                        .notes(assignment.getNotes())
                    .doneAt(assignment.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping assignment to shipper order history DTO: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing assignment data", e);
        }
    }
}


