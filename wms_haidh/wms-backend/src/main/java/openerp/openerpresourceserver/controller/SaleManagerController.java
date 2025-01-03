package openerp.openerpresourceserver.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
import openerp.openerpresourceserver.entity.projection.ReceiptInfoProjection;
import openerp.openerpresourceserver.entity.projection.ReceiptItemDetailProjection;
import openerp.openerpresourceserver.service.CustomerAddressService;
import openerp.openerpresourceserver.service.OrderService;
import openerp.openerpresourceserver.service.ReceiptService;
import openerp.openerpresourceserver.service.SaleOrderItemService;

@RestController
@RequestMapping("/sale-manager")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SaleManagerController {

	private ReceiptService receiptService;
	private OrderService orderService;
	private CustomerAddressService customerAddressService;
	private SaleOrderItemService saleOrderItemService;

	@GetMapping("/receipts")
	public ResponseEntity<Page<ReceiptInfoProjection>> getReceiptsByStatus(

			@RequestParam(defaultValue = "CREATED") String status, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<ReceiptInfoProjection> receipts = receiptService.searchReceipts(status, pageable);
			return ResponseEntity.ok(receipts);
		} catch (Exception e) {
			// Log the error for further investigation
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/receipts/{receiptId}")
	public ResponseEntity<List<ReceiptItemDetailProjection>> getReceiptItemDetails(@PathVariable UUID receiptId) {
		try {
			List<ReceiptItemDetailProjection> receiptItemDetails = receiptService.getReceiptItemDetails(receiptId);

			if (receiptItemDetails.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
			}
			return ResponseEntity.ok(receiptItemDetails);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
		}
	}

	@PostMapping("/receipts/approve/{receiptId}")
	public ResponseEntity<String> approveReceipt(@PathVariable UUID receiptId, @RequestParam String approvedBy) {
		try {
			boolean isApproved = receiptService.approveReceipt(receiptId, approvedBy);

			if (isApproved) {
				return ResponseEntity.ok("Receipt approved successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receipt not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error approving receipt.");
		}
	}

	@PostMapping("/receipts/cancel/{receiptId}")
	public ResponseEntity<String> cancelReceipt(@PathVariable UUID receiptId, @RequestParam String cancelledBy) {
		try {
			boolean isCancelled = receiptService.cancelReceipt(receiptId, cancelledBy);

			if (isCancelled) {
				return ResponseEntity.ok("Receipt cancelled successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receipt not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error cancelling receipt.");
		}
	}

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
