package openerp.openerpresourceserver.service;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.OrderItem;
import openerp.openerpresourceserver.repo.OrderItemRepo;
import openerp.openerpresourceserver.repo.OrderRepo;
import openerp.openerpresourceserver.request.OrderRequest;
import openerp.openerpresourceserver.response.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.util.List;
import java.util.UUID;


public interface OrderService {
    Order createOrder(Principal principal, OrderRequest order);


    // Get all orders method
    List<OrderResponse> getAllOrders();

    // Get order by ID method
    Order getOrderById(UUID orderId);


    // Edit order method
    @Transactional
    Order editOrder(UUID orderId, OrderRequest orderREQ);

    // Delete order method
    @Transactional
    void deleteOrder(UUID orderId);
}
