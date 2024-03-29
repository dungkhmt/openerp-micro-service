package openerp.containertransport.service;

import openerp.containertransport.dto.OrderFilterRequestDTO;
import openerp.containertransport.dto.OrderModel;
import openerp.containertransport.dto.OrderUpdateDTO;
import openerp.containertransport.dto.OrdersRes;
import openerp.containertransport.dto.dashboard.DashboardTimeOrderDTO;
import openerp.containertransport.entity.Order;

import java.util.List;

public interface OrderService {
    List<OrderModel> createOrder(OrderModel orderModel);
    OrdersRes filterOrders(OrderFilterRequestDTO orderFilterRequestDTO);
    OrderModel updateOrder(OrderModel orderModel);
    OrderModel getOrderByUid(String uid, String username, boolean isCustomer);
    OrderModel updateOrder(long id, OrderModel orderModel);
    OrderModel updateOrderByUid(String orderCode, OrderModel orderModel);
    List<OrderModel> updateListOrder(OrderUpdateDTO orderUpdateDTO);
    Long countOrderByMonth(DashboardTimeOrderDTO dashboardTimeOrderDTO, String status);
    OrderModel deleteOrder(String uid);
}
