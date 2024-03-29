package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.OrderDTO;
import openerp.openerpresourceserver.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<Order> findAllByCustomerId(String userId);

    List<Order> findAll();

    Order createOrder(OrderDTO orderDTO, String userId);

    Order updateOrder(UUID orderId, OrderDTO orderDTO, String userId);

    boolean deleteOrder(UUID id);

    Order getOrder(UUID id);






}
