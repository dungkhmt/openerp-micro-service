package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.dto.OrderResponseDto;
import openerp.openerpresourceserver.dto.OrderSummaryDTO;
import openerp.openerpresourceserver.dto.OrderSummaryMiddleMileDto;
import openerp.openerpresourceserver.entity.enumentity.RouteDirection;

import java.util.List;
import java.util.UUID;

public interface OrderRepositoryCustom {
    List<OrderSummaryDTO> findOrdersCreatedToday(UUID hubId);

    OrderResponseDto findOrderDetailById(UUID id);


    List<OrderSummaryDTO> getCollectedColelctorList(UUID hubId);

    List<OrderSummaryDTO> getCollectedHubList(UUID hubId);

    // Search for available order for vehicle
    List<OrderSummaryMiddleMileDto> getCollectedCollectorListVehicle(UUID vehicleId, UUID hubId);

}
