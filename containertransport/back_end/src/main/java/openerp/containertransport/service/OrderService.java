package openerp.containertransport.service;

import openerp.containertransport.dto.OrderFilterRequestDTO;
import openerp.containertransport.dto.OrderModel;
import openerp.containertransport.dto.OrdersRes;
import openerp.containertransport.entity.Order;

import java.util.List;

public interface OrderService {
    List<OrderModel> createOrder(OrderModel orderModel);
    OrdersRes filterOrders(OrderFilterRequestDTO orderFilterRequestDTO);
    OrderModel updateOrder(OrderModel orderModel);
    OrderModel getOrderByOrderCode(String orderCode, String username);
    OrderModel updateOrder(long id, OrderModel orderModel);
    OrderModel updateOrderByCode(String orderCode, OrderModel orderModel);
}
