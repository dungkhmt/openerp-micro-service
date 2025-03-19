package openerp.openerpresourceserver.service;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.dto.OrderResponseDto;
import openerp.openerpresourceserver.dto.OrderSummaryMiddleMileDto;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface MiddleMileOrderService {
    @Transactional
    void assignOrdersToTrip(UUID routeVehicleId, List<UUID> orderIds);

    @Transactional
    void unassignOrderFromTrip(UUID orderId);

    List<OrderResponseDto> getOrdersByTrip(UUID routeVehicleId);

    @Transactional
    void updateOrderStatus(UUID orderId, OrderStatus status);

    @Transactional
    void completeTrip(UUID routeVehicleId);


    List<OrderSummaryMiddleMileDto> getCollectedHubListVehicle(UUID vehicleId, UUID hubId);
}
