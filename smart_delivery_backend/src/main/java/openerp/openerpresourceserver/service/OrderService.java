package openerp.openerpresourceserver.service;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.RouteSchedule;
import openerp.openerpresourceserver.entity.Vehicle;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
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

    List<TodayAssignmentDto> getAssignmentTodayByHubId(UUID hubId);

    List<AssignOrderCollectorDTO> getAssignmentTodayByCollectorId(UUID collectorId);


    List<OrderSummaryDTO> getCollectedCollectorList(UUID hubId);

    List<OrderSummaryDTO> getCollectedHubList(UUID hubId);

    boolean confirmCollectedHub(Principal principal, UUID[] orderId);

    boolean confirmOutHub(Principal principal, UUID[] orderIds,  UUID vehicleId);


    List<OrderItemForTripDto> getOrderItemsForTrip(UUID tripId);

}
