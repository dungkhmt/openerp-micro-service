package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.request.OrderRequest;
import openerp.openerpresourceserver.response.OrderResponse;
import openerp.openerpresourceserver.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/ordermanager")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Create an order
    @PostMapping("/order/add")
    public ResponseEntity<Order> createOrder(Principal principal, @Valid @RequestBody OrderRequest orderRequest) {
        Order createdOrder = orderService.createOrder(principal, orderRequest);
        return ResponseEntity.ok(createdOrder);
    }

    // Get all orders
    @GetMapping("/order")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Get order by ID
    @GetMapping("/order/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // Edit an existing order
    @PutMapping("/order/update/{id}")
    public ResponseEntity<Order> editOrder(@PathVariable UUID id, @Valid @RequestBody OrderRequest orderRequest) {
        Order updatedOrder = orderService.editOrder(id, orderRequest);
        return ResponseEntity.ok(updatedOrder);
    }

    // Delete an order
    @DeleteMapping("/order/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();  // Return 204 No Content on successful deletion
    }
}
