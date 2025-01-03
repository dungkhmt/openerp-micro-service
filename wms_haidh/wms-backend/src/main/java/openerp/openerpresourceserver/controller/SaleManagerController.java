package openerp.openerpresourceserver.controller;

import java.util.List;
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
import openerp.openerpresourceserver.entity.CustomerAddress;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.projection.OrderDetailProjection;
import openerp.openerpresourceserver.entity.projection.OrderProjection;
import openerp.openerpresourceserver.service.CustomerAddressService;
import openerp.openerpresourceserver.service.OrderService;
import openerp.openerpresourceserver.service.SaleOrderItemService;

@RestController
@RequestMapping("/sale-manager")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SaleManagerController {

	private OrderService orderService;
	private CustomerAddressService customerAddressService;
	private SaleOrderItemService saleOrderItemService;

	@GetMapping("/orders")
	public ResponseEntity<Page<OrderProjection>> getOrders(@RequestParam(defaultValue = "CREATED") String status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<OrderProjection> orders = orderService.getOrders(status, pageable);
		return ResponseEntity.ok(orders);
	}

	@GetMapping("/orders/{orderId}")
	public ResponseEntity<Order> getOrderById(@PathVariable UUID orderId) {
		Order order = orderService.getOrderById(orderId);
		return ResponseEntity.ok(order);
	}

	@GetMapping("/orders/customer-address/{orderId}")
	public ResponseEntity<CustomerAddress> getCustomerAddressByOrderId(@PathVariable UUID orderId) {
		CustomerAddress customerAddress = customerAddressService.getCustomerAddressByOrderId(orderId);
		return ResponseEntity.ok(customerAddress);
	}

	@GetMapping("/orders/order-item/{orderId}")
	public ResponseEntity<List<OrderDetailProjection>> getOrderDetailsByOrderId(@PathVariable UUID orderId) {
		List<OrderDetailProjection> orderDetails = saleOrderItemService.getOrderDetailsByOrderId(orderId);
		return ResponseEntity.ok(orderDetails);
	}

	@PostMapping("/orders/approve/{orderId}")
	public ResponseEntity<Order> approveOrder(@PathVariable UUID orderId, @RequestParam String approvedBy) {
		Order approvedOrder = orderService.approveOrder(orderId, approvedBy);
		return ResponseEntity.ok(approvedOrder);
	}

	@PostMapping("/orders/cancel/{orderId}")
	public ResponseEntity<Order> cancelOrder(@PathVariable UUID orderId, @RequestParam String cancelledBy) {
		Order cancelledOrder = orderService.cancelOrder(orderId, cancelledBy);
		return ResponseEntity.ok(cancelledOrder);
	}

}
