package openerp.openerpresourceserver.service;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.RouteSchedule;
import openerp.openerpresourceserver.entity.Vehicle;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface OrderService {
    Order createOrder(Principal principal, OrderRequestDto order);


    // Get all orders method
    List<OrderSummaryDTO> getAllOrders();

    // Get order by ID method
    OrderResponseDto getOrderById(UUID orderId);


    // Edit order method
    @Transactional
    Order editOrder(Principal principal,UUID orderId, OrderRequestDto orderREQ);

    // Delete order method
    @Transactional
    void deleteOrder(Principal principal, UUID orderId);

    List<OrderSummaryDTO> getAllOrdersByHubIdToday(UUID hubId);

    List<OrderSummaryDTO> getAllOrdersByHubId(UUID hubId);

    List<OrderResponseCollectorShipperDto> autoAssignOrderToCollector(UUID hubId, List<OrderRequestDto> orders, List<EmployeeDTO> collectors);

//    List<TodayAssignmentDto> getAssignmentTodayByHubId(UUID hubId);

//    List<AssignOrderCollectorDTO> getAssignmentTodayByCollectorId(UUID collectorId);


    List<OrderSummaryDTO> getCollectedCollectorList(UUID hubId);

    List<OrderSummaryDTO> getCollectedHubList(UUID hubId);

    boolean confirmCollectedHub(Principal principal, UUID[] orderId);

    boolean confirmOutHub(Principal principal, UUID[] orderIds,  UUID vehicleId);



    List<OrderSummaryDTO> getAllOrdersDeliveredInHub(UUID hubId);

    List<Order> getCustomerOrders(UUID customerId);


    List<OrderSummaryDTO> getOrderByUsername(String username);
    /**
     * Get orders collected by collectors and brought to hub (for InOrder tab 1)
     */
    List<OrderSummaryDTO> getCollectedCollectorOrders(UUID hubId);

    /**
     * Get orders delivered to hub by drivers from other hubs (for InOrder tab 2)
     */
    List<TripOrderSummaryDto> getDeliveredDriverOrders(UUID hubId);

    /**
     * Get orders with failed delivery attempts (for InOrder tab 3)
     */
    List<OrderSummaryDTO> getFailedDeliveryOrders(UUID hubId);

    /**
     * Confirm orders into hub from various sources
     */
    boolean confirmOrdersIntoHub(Principal principal, UUID[] orderIds);

    /**
     * Confirm single shipper pickup (for OutOrder shipper tab)
     */
    boolean confirmShipperPickup(Principal principal, UUID shipperId);

    /**
     * Confirm multiple shipper pickups (for OutOrder shipper tab)
     */
    boolean confirmMultipleShipperPickups(Principal principal, UUID[] shipperIds);

    /**
     * Get shipper pickup requests - shippers with assigned orders waiting for pickup
     */
    List<TodayAssignmentShipperDto> getShipperPickupRequests(UUID hubId);
    // New methods for order history
    List<CollectorOrderHistoryDto> getCollectorOrderHistory(UUID collectorId, LocalDate startDate, LocalDate endDate);
    List<ShipperOrderHistoryDto> getShipperOrderHistory(UUID shipperId, LocalDate startDate, LocalDate endDate);
//    /**
//     * Tạo đề xuất phân công mà không lưu vào database
//     */
//    List<OrderResponseCollectorShipperDto> suggestOrderToCollectorAssignment(
//            UUID hubId,
//            List<OrderRequestDto> orders,
//            List<EmployeeDTO> collectors
//    );

//    /**
//     * Xác nhận và lưu phân công vào database
//     */
//    List<OrderResponseCollectorShipperDto> confirmOrderToCollectorAssignment(
//            Principal principal,
//            UUID hubId,
//            List<ConfirmAssignmentDto.AssignmentDetailDto> assignments
//    );

    List<OrderSummaryDTO> getFailedShippedOrders(UUID hubId);
}
