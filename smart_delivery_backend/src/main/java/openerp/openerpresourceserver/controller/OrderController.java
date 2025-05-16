package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import openerp.openerpresourceserver.service.AssignmentService;
import openerp.openerpresourceserver.service.OrderHistoryService;
import openerp.openerpresourceserver.service.OrderService;
import openerp.openerpresourceserver.service.ShipperAssignmentService;
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
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderHistoryService orderHistoryService;
    private final AssignmentService assignmentService;
    private final ShipperAssignmentService shipperAssignmentService;
    // Dùng HashMap làm bộ nhớ tạm, có thể thay thế bằng Redis để lưu trữ lâu dài
    private Map<UUID, LocalDate> lastAssignedDate = new HashMap<>();



    // Create an order
    @PostMapping("/order/add")
    public ResponseEntity<Order> createOrder(Principal principal, @Valid @RequestBody OrderRequestDto orderRequest) {
        System.out.println("OrderController.createOrder: " + orderRequest);
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

    @GetMapping("/order/sender/{username}")
    public ResponseEntity<List<OrderSummaryDTO>> getAllOrdersByCreatedByUsername(@PathVariable String username) {
        return ResponseEntity.ok(orderService.getOrderByUsername(username));
    }


    @GetMapping("/order/hub/today/{hubId}")
    public ResponseEntity<List<OrderSummaryDTO>> getAllOrdersTodayByHubId(@PathVariable UUID hubId){
        return ResponseEntity.ok(orderService.getAllOrdersByHubIdToday(hubId));
    }

    @GetMapping("/order/delivered/hub/{hubId}")
    public ResponseEntity<List<OrderSummaryDTO>> getAllOrdersDeliveredInHub(@PathVariable UUID hubId) {
        return ResponseEntity.ok(orderService.getAllOrdersDeliveredInHub(hubId));
    }

    // Get order by ID
    @GetMapping("/order/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // Edit an existing order
    @PutMapping("/order/update/{id}")
    public ResponseEntity<Order> updateOrder(Principal principal, @PathVariable UUID id, @Valid @RequestBody OrderRequestDto orderRequest) {
        Order updatedOrder = orderService.editOrder(principal, id, orderRequest);
        return ResponseEntity.ok(updatedOrder);
    }

    // Delete an order
    @DeleteMapping("/order/delete/{id}")
    public ResponseEntity<Void> deleteOrder(Principal principal, @PathVariable UUID id) {
        orderService.deleteOrder(principal,id);
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
                request.getEmployees());

        // Lưu trữ thời gian gọi API
        lastAssignedDate.put(userId, today);

        return ResponseEntity.ok(response);
    }

    // Get
    @GetMapping("/order/assign/collector/today/{hubId}")
    public ResponseEntity<List<TodayAssignmentDto>> getAssignmentTodayByHubId(@PathVariable UUID hubId) {
        return ResponseEntity.ok(orderService.getAssignmentTodayByHubId(hubId));
    }
    @GetMapping("/order/assign/hub/shipper/today/{hubId}")
    public ResponseEntity<List<TodayAssignmentShipperDto>> getShipperAssignmentTodayByHubId(@PathVariable UUID hubId) {
        return ResponseEntity.ok(shipperAssignmentService.getShipperAssignmentsTodayByHub(hubId));
    }
    @GetMapping("/order/assign/shipper/today/{shipperId}")
    public ResponseEntity<List<AssignOrderShipperDto>> getShipperAssignmentTodayByShipperId(@PathVariable UUID shipperId) {
        return ResponseEntity.ok(shipperAssignmentService.getShipperAssignmentsToday(shipperId));
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

    /**
     * Update assignment status
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SHIPPER')")
    @PutMapping("/order/assignment/shipper")
    public ResponseEntity<?> updateShipperAssignment(@Valid @RequestBody UpdateShipperAssignmentRequestDto request) {
        try {
            shipperAssignmentService.updateAssignmentStatus(request.getAssignmentId(), request.getStatus());
            return ResponseEntity.ok("Assignment updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update assignment: " + e.getMessage());
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
    public ResponseEntity<String> confirmCollectedHub(Principal principal, @PathVariable UUID[] orderIds) {
        return orderService.confirmCollectedHub(principal, orderIds) ? ResponseEntity.ok("OK") :
                new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
    }


        @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER')")
        @PutMapping("/out-hub/complete/{orderIds}/{vehicleId}")
        public ResponseEntity<String> confirmOutHub(Principal principal, @PathVariable UUID[] orderIds, @PathVariable UUID vehicleId) {
            return orderService.confirmOutHub(principal,orderIds, vehicleId) ? ResponseEntity.ok("OK") :
                    new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @PreAuthorize("hasAnyRole('ROUTE_MANAGER', 'DRIVER', 'HUB_STAFF')")
        @GetMapping("/middle-mile/for-out/{tripId}")
        public ResponseEntity<List<OrderItemForTripDto>> getMiddleMileOrdersForOut(@PathVariable  UUID tripId) {
            return ResponseEntity.ok(orderService.getOrderItemsForTrip(tripId));
        }

    /**
     * Get the complete history of an order
     */
    @GetMapping("/order/{orderId}/history")
    public ResponseEntity<List<OrderHistoryResponseDto>> getOrderHistory(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderHistoryService.getOrderHistory(orderId));
    }

}
