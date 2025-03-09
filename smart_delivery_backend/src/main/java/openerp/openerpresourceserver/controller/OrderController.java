package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import openerp.openerpresourceserver.service.AssignmentService;
import openerp.openerpresourceserver.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/ordermanager")
public class OrderController {

    private final OrderService orderService;

    private final AssignmentService assignmentService;

    // Dùng HashMap làm bộ nhớ tạm, có thể thay thế bằng Redis để lưu trữ lâu dài
    private Map<UUID, LocalDate> lastAssignedDate = new HashMap<>();

    @Autowired
    public OrderController(OrderService orderService, AssignmentService assignmentService) {
        this.orderService = orderService;
        this.assignmentService = assignmentService;
    }

    // Create an order
    @PostMapping("/order/add")
    public ResponseEntity<Order> createOrder(Principal principal, @Valid @RequestBody OrderRequestDto orderRequest) {
        Order createdOrder = orderService.createOrder(principal, orderRequest);
        return ResponseEntity.ok(createdOrder);
    }

    // Get all orders
    @GetMapping("/order")
    public ResponseEntity<List<OrderSummaryDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/order/hub/{hubId}")
    public ResponseEntity<List<OrderSummaryDTO>> getAllOrdersByHubId(@PathVariable UUID hubId) {
        return ResponseEntity.ok(orderService.getAllOrdersByHubId(hubId));
    }

    @GetMapping("/order/hub/today/{hubId}")
    public ResponseEntity<List<OrderSummaryDTO>> getAllOrdersTodayByHubId(@PathVariable UUID hubId){
        return ResponseEntity.ok(orderService.getAllOrdersByHubIdToday(hubId));
    }


    // Get order by ID
    @GetMapping("/order/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // Edit an existing order
    @PutMapping("/order/update/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable UUID id, @Valid @RequestBody OrderRequestDto orderRequest) {
        Order updatedOrder = orderService.editOrder(id, orderRequest);
        return ResponseEntity.ok(updatedOrder);
    }

    // Delete an order
    @DeleteMapping("/order/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();  // Return 204 No Content on successful deletion
    }

    @PostMapping("/order/assign/collector")
    public ResponseEntity<List<OrderResponseCollectorShipperDto>> assignOrderToCollector(@RequestBody AssignOrderDto request) {
        // Xử lý kiểm tra ngày đã gọi API
        UUID userId = request.getHubId(); // Hoặc sử dụng một ID người dùng cụ thể nếu cần
        LocalDate today = LocalDate.now();

        if (lastAssignedDate.containsKey(userId)) {
            LocalDate lastAssigned = lastAssignedDate.get(userId);
//            if (lastAssigned.equals(today)) {
//                // Nếu đã gọi API trong ngày, trả về lỗi
//                return ResponseEntity.badRequest().body(null);
//            }
        }

        // Nếu chưa gọi API hôm nay, tiếp tục xử lý yêu cầu
        List<OrderResponseCollectorShipperDto> response = orderService.autoAssignOrderToCollector(request.getHubId(),
                request.getOrders(),
                request.getCollectors());

        // Lưu trữ thời gian gọi API
        lastAssignedDate.put(userId, today);

        return ResponseEntity.ok(response);
    }

    // Get
    @GetMapping("/order/assign/today/{hubId}")
    public ResponseEntity<List<TodayAssignmentDto>> getAssignmentTodayByHubId(@PathVariable UUID hubId) {
        return ResponseEntity.ok(orderService.getAssignmentTodayByHubId(hubId));
    }


    @GetMapping("/order/assign/today/collector/{collectorId}")
    public ResponseEntity<List<AssignOrderCollectorDTO>> getAssignmentTodayByCollectorId(@PathVariable UUID collectorId) {
        return ResponseEntity.ok(orderService.getAssignmentTodayByCollectorId(collectorId));
    }

    @PutMapping("/order/assignment/collector")
    public ResponseEntity<?> updateAssignment(@RequestBody UpdateAssignmentRequest request) {
        try {
            assignmentService.updateAssignmentStatus(request.getAssignmentId(), request.getStatus());
            return ResponseEntity.ok("Assignment updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER')")
    @PutMapping("/order/assignment/pickups/confirmation/{orderId}")
    public  ResponseEntity<?> confirmOrderInHub(@PathVariable UUID orderId){

        return null;
    }

    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER')")
    @GetMapping("/order/collected-collector/{hubId}")
    public  ResponseEntity<List<OrderSummaryDTO>> getCollectedCollectorList(@PathVariable UUID hubId){
           return ResponseEntity.ok(orderService.getCollectedCollectorList(hubId));
    }

    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER')")
    @GetMapping("/order/collected-hub/{hubId}")
    public  ResponseEntity<List<OrderSummaryDTO>> getCollectedHubList(@PathVariable UUID hubId){
        return ResponseEntity.ok(orderService.getCollectedHubList(hubId));
    }
//    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER')")
//    @GetMapping("/order/collected-hub/{vehicleId}/{hubId}")
//    public  ResponseEntity<List<OrderSummaryDTO>> getCollectedHubListVehicle(@PathVariable UUID hubId,@PathVariable UUID vehicleId){
//        return ResponseEntity.ok(orderService.getCollectedHubListVehicle(vehicleId, hubId));
//    }


    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER')")
    @PutMapping("/collected-hub/complete/{orderIds}")
    public ResponseEntity<String> confirmCollectedHub(@PathVariable UUID[] orderIds) {
        return orderService.confirmCollectedHub(orderIds) ? ResponseEntity.ok("OK") :
                new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
    }


        @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER')")
        @PutMapping("/out-hub/complete/{orderIds}/{vehicleId}")
        public ResponseEntity<String> confirmOutHub(@PathVariable UUID[] orderIds, @PathVariable UUID vehicleId) {
            return orderService.confirmOutHub(orderIds, vehicleId) ? ResponseEntity.ok("OK") :
                    new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
        }

}
