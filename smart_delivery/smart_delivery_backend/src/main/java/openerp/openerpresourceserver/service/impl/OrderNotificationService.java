package openerp.openerpresourceserver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.service.NotificationsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderNotificationService {

    private final NotificationsService notificationsService;
    
    private static final Map<OrderStatus, String> STATUS_MESSAGES = new HashMap<>();
    
    static {
        STATUS_MESSAGES.put(OrderStatus.PENDING, "Đơn hàng của bạn đang được xử lý");
        STATUS_MESSAGES.put(OrderStatus.ASSIGNED, "Đơn hàng của bạn đã được phân công cho nhân viên thu gom");
        STATUS_MESSAGES.put(OrderStatus.COLLECTED_COLLECTOR, "Nhân viên thu gom đã lấy hàng của bạn");
        STATUS_MESSAGES.put(OrderStatus.COLLECTED_HUB, "Đơn hàng của bạn đã về Hub nguồn");
        STATUS_MESSAGES.put(OrderStatus.DELIVERING, "Đơn hàng của bạn đang được vận chuyển");
        STATUS_MESSAGES.put(OrderStatus.DELIVERED, "Đơn hàng của bạn đã đến Hub đích");
        STATUS_MESSAGES.put(OrderStatus.ASSIGNED_SHIPPER, "Đơn hàng của bạn đã được phân công cho shipper");
        STATUS_MESSAGES.put(OrderStatus.SHIPPED, "Shipper đang giao hàng cho bạn");
        STATUS_MESSAGES.put(OrderStatus.COMPLETED, "Đơn hàng của bạn đã được giao thành công");
        STATUS_MESSAGES.put(OrderStatus.CANCELLED, "Đơn hàng của bạn đã bị hủy");
    }

    public void sendOrderStatusChangeNotification(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        try {
            String message = STATUS_MESSAGES.getOrDefault(newStatus, "Trạng thái đơn hàng của bạn đã thay đổi");
            String url = "/orders/" + order.getId(); // URL to view order details
            
            // Send notification to sender
            notificationsService.create(
                "SYSTEM", // fromUser
                order.getSenderId().toString(), // toUser
                message,
                url
            );
            
            log.info("Sent order status change notification for order {}: {} -> {}", 
                order.getId(), oldStatus, newStatus);
        } catch (Exception e) {
            log.error("Failed to send order status change notification for order {}: {}", 
                order.getId(), e.getMessage());
        }
    }
} 