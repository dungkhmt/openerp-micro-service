package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.dto.OrderResponseDto;
import openerp.openerpresourceserver.dto.OrderSummaryDTO;
import openerp.openerpresourceserver.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRepositoryCustom {
    List<OrderSummaryDTO> findOrdersCreatedToday(UUID hubId);

    OrderResponseDto findOrderDetailById(UUID id);


    List<OrderSummaryDTO> getCollectedColelctorList(UUID hubId);

    List<OrderSummaryDTO> getCollectedHubList(UUID hubId);
}
