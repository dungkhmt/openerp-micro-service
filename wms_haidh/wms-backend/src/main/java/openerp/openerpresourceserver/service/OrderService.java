package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.SaleOrderCreateRequest;
import openerp.openerpresourceserver.dto.request.SaleOrderItemDTO;
import openerp.openerpresourceserver.dto.response.CustomerOrderResponse;
import openerp.openerpresourceserver.dto.response.OrderResponse;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.repository.OrderRepository;

@Service
@AllArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final SaleOrderItemService saleOrderItemService;
	private final ProductWarehouseService productWarehouseService;
	private final ProductService productService;

	public Page<OrderResponse> getOrders(String status, Pageable pageable) {

		return orderRepository.findOrdersByStatus(status, pageable);
	}

	public Page<OrderResponse> getOrdersByUserLoginId(String userLoginId, Pageable pageable) {
		return orderRepository.findOrdersByUserLoginId(userLoginId, pageable);
	}

	public Order getOrderById(UUID orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
	}

	public Optional<CustomerOrderResponse> getCustomerOrderById(UUID orderId) {
		return orderRepository.findCustomerOrderById(orderId);
	}

	public Order markOrderAsCompleted(UUID orderId) {
		Order order = getOrderById(orderId);
		order.setStatus("COMPLETED");
		order.setLastUpdatedStamp(LocalDateTime.now());
		return orderRepository.save(order);
	}

	@Transactional
	public void markOrdersAsDelivering(List<UUID> orderIds) {
		if (!orderIds.isEmpty()) {
			orderRepository.updateStatusToDelivering(orderIds);
		}
	}

	public Order approveOrder(UUID orderId, String approvedBy) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

		if (!"CREATED".equals(order.getStatus())) {
			throw new RuntimeException("Only orders with status 'CREATED' can be approved.");
		}

		order.setStatus("APPROVED");
		order.setApprovedBy(approvedBy);
		order.setLastUpdatedStamp(LocalDateTime.now());
		return orderRepository.save(order);
	}

	public Order cancelOrder(UUID orderId, String cancelledBy) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

		if (!"CREATED".equals(order.getStatus())) {
			throw new RuntimeException("Only orders with status 'CREATED' can be canceled.");
		}

		order.setStatus("CANCELLED");
		order.setCancelledBy(cancelledBy);
		order.setLastUpdatedStamp(LocalDateTime.now());
		return orderRepository.save(order);
	}

	public Order createOrder(SaleOrderCreateRequest request, String userLoginId) {
		LocalDateTime now = LocalDateTime.now();
		Order order = Order.builder().userLoginId(userLoginId).deliveryFee(request.getDeliveryFee())
				.totalProductCost(request.getTotalProductCost()).totalOrderCost(request.getTotalOrderCost())
				.customerAddressId(request.getCustomerAddressId()).customerName(request.getCustomerName())
				.customerPhoneNumber(request.getCustomerPhoneNumber()).description(request.getDescription())
				.paymentType(request.getPaymentType()).orderType(request.getOrderType()).status("CREATED")
				.orderDate(now).createdStamp(now).lastUpdatedStamp(now).build();
		return orderRepository.save(order);
	}

	@Transactional
	public Order placeOrder(SaleOrderCreateRequest request, String userLoginId) {
		
		for (SaleOrderItemDTO item : request.getItems()) {
			boolean available = productWarehouseService.isProductAvailable(item.getProductId(), item.getQuantity());
			if (!available) {
				String name = productService.getProductNameById(item.getProductId());
				throw new RuntimeException("Not enough stock for product: " + name);
			}
		}

		Order order = createOrder(request, userLoginId);

		saleOrderItemService.createItems(request.getItems(), order.getOrderId());

		return order;
	}

}
