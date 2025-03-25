package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.projection.CustomerOrderProjection;
import openerp.openerpresourceserver.projection.OrderProjection;
import openerp.openerpresourceserver.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Page<OrderProjection> getOrders(String status, Pageable pageable) {
        
        return orderRepository.findOrdersByStatus(status, pageable);
    }
    
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }
    
    public Optional<CustomerOrderProjection> getCustomerOrderById(UUID orderId) {
        return orderRepository.findCustomerOrderById(orderId);
    }
    
    public Order distributeOrder(UUID orderId) {
    	Order order = getOrderById(orderId);
        order.setStatus("DISTRIBUTED");
        return orderRepository.save(order);
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
}

