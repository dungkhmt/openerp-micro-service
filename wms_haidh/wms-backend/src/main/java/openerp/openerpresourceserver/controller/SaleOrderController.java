package openerp.openerpresourceserver.controller;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.SaleOrderCreateRequest;
import openerp.openerpresourceserver.dto.response.CustomerOrderResponse;
import openerp.openerpresourceserver.dto.response.OrderResponse;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.service.OrderService;

@RestController
@RequestMapping("/orders")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SaleOrderController {

	private OrderService orderService;

	@Secured({"ROLE_WMS_WAREHOUSE_MANAGER","ROLE_WMS_SALE_MANAGER"})
	@GetMapping
	public ResponseEntity<Page<OrderResponse>> getOrders(@RequestParam(defaultValue = "CREATED") String status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<OrderResponse> orders = orderService.getOrders(status, pageable);
		return ResponseEntity.ok(orders);
	}

	@Secured("ROLE_WMS_ONLINE_CUSTOMER")
	@GetMapping("/by-user")
	public ResponseEntity<Page<OrderResponse>> getOrdersByUserLoginId(Principal principal,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<OrderResponse> orders = orderService.getOrdersByUserLoginId(principal.getName(), pageable);
		return ResponseEntity.ok(orders);
	}

	@Secured({"ROLE_WMS_WAREHOUSE_MANAGER","ROLE_WMS_SALE_MANAGER","ROLE_WMS_ONLINE_CUSTOMER"})
	@GetMapping("/{orderId}")
	public ResponseEntity<Order> getOrderById(@PathVariable UUID orderId) {
		Order order = orderService.getOrderById(orderId);
		return ResponseEntity.ok(order);
	}

	@Secured({"ROLE_WMS_WAREHOUSE_MANAGER","ROLE_WMS_SALE_MANAGER","ROLE_WMS_DELIVERY_MANAGER","ROLE_WMS_ONLINE_CUSTOMER"})
	@GetMapping("/{orderId}/customer-address")
	public ResponseEntity<CustomerOrderResponse> getCustomerOrderById(@PathVariable UUID orderId) {
		Optional<CustomerOrderResponse> order = orderService.getCustomerOrderById(orderId);
		return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@Secured("ROLE_WMS_ONLINE_CUSTOMER")
	@PostMapping
	public ResponseEntity<String> createOrder(@RequestBody SaleOrderCreateRequest request, Principal principal) {
	    try {
	        Order order = orderService.placeOrder(request, principal.getName());
	        return ResponseEntity.ok("Order created with ID: " + order.getOrderId());
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	}

	@Secured("ROLE_WMS_SALE_MANAGER")
	@PostMapping("/{orderId}/approve")
	public ResponseEntity<Order> approveOrder(@PathVariable UUID orderId, Principal principal) {
		Order approvedOrder = orderService.approveOrder(orderId, principal.getName());
		return ResponseEntity.ok(approvedOrder);
	}

	@Secured({"ROLE_WMS_SALE_MANAGER","ROLE_WMS_ONLINE_CUSTOMER"})
	@PostMapping("/{orderId}/cancel")
	public ResponseEntity<Order> cancelOrder(@PathVariable UUID orderId, Principal principal) {
		Order cancelledOrder = orderService.cancelOrder(orderId, principal.getName());
		return ResponseEntity.ok(cancelledOrder);
	}

}
