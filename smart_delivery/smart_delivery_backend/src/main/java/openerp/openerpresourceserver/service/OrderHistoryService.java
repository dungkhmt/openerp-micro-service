package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.OrderHistoryResponseDto;

import java.util.List;
import java.util.UUID;

public interface OrderHistoryService {
    List<OrderHistoryResponseDto> getOrderHistory(UUID orderId);
} 