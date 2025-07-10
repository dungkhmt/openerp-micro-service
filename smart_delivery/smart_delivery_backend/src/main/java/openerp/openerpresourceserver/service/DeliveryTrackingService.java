package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface DeliveryTrackingService {

    /**
     * Update the status of an order in the delivery phase
     */
    void updateOrderStatus(UUID orderId, OrderStatus status, String notes);

    /**
     * Get all orders with a specific status at a hub
     */
    List<Order> getOrdersByStatusAndHub(UUID hubId, OrderStatus status);

    /**
     * Get the full delivery history of an order
     */
    List<OrderStatus> getOrderStatusHistory(UUID orderId);

    /**
     * Get the current location or status of an order
     */
    String getOrderTrackingInfo(UUID orderId);
}
