package openerp.openerpresourceserver.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.projection.CustomerOrderProjection;
import openerp.openerpresourceserver.entity.projection.OrderProjection;
import openerp.openerpresourceserver.service.OrderService;

@RestController
@RequestMapping("/orders")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SaleOrderController {

	private OrderService orderService;
	
	@GetMapping
	public ResponseEntity<Page<OrderProjection>> getOrders(@RequestParam(defaultValue = "CREATED") String status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<OrderProjection> orders = orderService.getOrders(status, pageable);
		return ResponseEntity.ok(orders);
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<Order> getOrderById(@PathVariable UUID orderId) {
		Order order = orderService.getOrderById(orderId);
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/{orderId}/customer-address")
	public ResponseEntity<CustomerOrderProjection> getCustomerOrderById(@PathVariable UUID orderId) {
		Optional<CustomerOrderProjection> order = orderService.getCustomerOrderById(orderId);
		return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@PostMapping("/{orderId}/approve")
	public ResponseEntity<Order> approveOrder(@PathVariable UUID orderId, @RequestParam String approvedBy) {
		Order approvedOrder = orderService.approveOrder(orderId, approvedBy);
		return ResponseEntity.ok(approvedOrder);
	}

	@PostMapping("/{orderId}/cancel")
	public ResponseEntity<Order> cancelOrder(@PathVariable UUID orderId, @RequestParam String cancelledBy) {
		Order cancelledOrder = orderService.cancelOrder(orderId, cancelledBy);
		return ResponseEntity.ok(cancelledOrder);
	}

}
