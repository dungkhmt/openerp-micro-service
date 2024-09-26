package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.OrderItem;
import openerp.openerpresourceserver.entity.Recipient;
import openerp.openerpresourceserver.entity.Sender;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.repo.*;
import openerp.openerpresourceserver.request.OrderRequest;
import openerp.openerpresourceserver.response.OrderResponse;
import openerp.openerpresourceserver.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Autowired
    private SenderRepo senderRepo;

    @Autowired
    private RecipientRepo recipientRepo;

    // Create order method
    @Override
    @Transactional
    public Order createOrder(Principal principal, OrderRequest orderREQ) {
        Order orderEntity = new Order();
        Sender sender = senderRepo.findByName(orderREQ.getSenderName());
        Recipient recipient = recipientRepo.findByName(orderREQ.getRecipientName());

        if (sender == null) {
            sender = new Sender(orderREQ.getSenderName(), orderREQ.getSenderPhone(), orderREQ.getSenderEmail(), orderREQ.getSenderAddress());
        }

        if (recipient == null) {
            recipient = new Recipient(orderREQ.getRecipientName(), orderREQ.getRecipientPhone(), orderREQ.getRecipientEmail(), orderREQ.getRecipientAddress());
        }

        orderEntity.setStatus(OrderStatus.PENDING);
        orderEntity.setTotalPrice(orderREQ.getTotalprice());
        orderEntity.setSender(sender);
        orderEntity.setRecipient(recipient);
        orderEntity.setOrderType(orderREQ.getOrderType());

        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItem> items = orderREQ.getItems();

        for (OrderItem item : items) {
            OrderItem orderItem = new OrderItem(item.getName(), item.getQuantity(), item.getWeight(), item.getPrice(), item.getLength(), item.getWidth(), item.getHeight());
            orderItem.setOrder(orderEntity);
            orderItems.add(orderItem);
        }

        orderEntity.setItems(orderItems);
        orderEntity.setCreatedBy(principal.getName());
        logger.info("Created Order: {}", orderEntity);

        return orderRepo.save(orderEntity);
    }

    // Get all orders method
    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orderList = orderRepo.findAll();
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orderList) {
            // Tạo OrderResponse cho từng Order
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setId(order.getId());
            orderResponse.setSender(order.getSender());
            orderResponse.setRecipient(order.getRecipient());
            orderResponse.setTotalPrice(order.getTotalPrice());
            orderResponse.setCreatedAt(order.getCreatedAt());
            orderResponse.setOrderType(order.getOrderType());


            // Thêm vào danh sách orderResponses
            orderResponses.add(orderResponse);
        }
        return orderResponses;
    }

    // Get order by ID method
    @Override
    public Order getOrderById(UUID orderId) {
        return orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    // Edit order method
    @Override
    @Transactional
    public Order editOrder(UUID orderId, OrderRequest orderREQ) {
        Order existingOrder = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        Sender sender = senderRepo.findByName(orderREQ.getSenderName());
        Recipient recipient = recipientRepo.findByName(orderREQ.getRecipientName());

        if (sender == null) {
            sender = new Sender(orderREQ.getSenderName(), orderREQ.getSenderPhone(), orderREQ.getSenderEmail(), orderREQ.getSenderAddress());
        }

        if (recipient == null) {
            recipient = new Recipient(orderREQ.getRecipientName(), orderREQ.getRecipientPhone(), orderREQ.getRecipientEmail(), orderREQ.getRecipientAddress());
        }

        existingOrder.setSender(sender);
        existingOrder.setRecipient(recipient);
        existingOrder.setTotalPrice(orderREQ.getTotalprice());

        // Clear existing items and add new ones
        orderItemRepo.deleteAllByOrderId(orderId);
        List<OrderItem> updatedOrderItems = new ArrayList<>();
        for (OrderItem item : orderREQ.getItems()) {
            OrderItem newOrderItem = new OrderItem(item.getName(), item.getQuantity(), item.getWeight(), item.getPrice(), item.getLength(), item.getWidth(), item.getHeight());
            newOrderItem.setOrder(existingOrder);
            updatedOrderItems.add(newOrderItem);
        }
        existingOrder.setItems(updatedOrderItems);
        existingOrder.setStatus(OrderStatus.PENDING);

        return orderRepo.save(existingOrder);
    }

    // Delete order method
    @Override
    @Transactional
    public void deleteOrder(UUID orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        orderItemRepo.deleteAllByOrderId(orderId);
        orderRepo.deleteById(orderId);
        logger.info("Deleted Order with ID: {}", orderId);
    }
}
