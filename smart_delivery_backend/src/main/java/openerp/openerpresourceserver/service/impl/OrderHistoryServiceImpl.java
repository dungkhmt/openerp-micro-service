package openerp.openerpresourceserver.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.OrderHistoryResponseDto;
import openerp.openerpresourceserver.entity.OrderHistory;
import openerp.openerpresourceserver.repository.OrderHistoryRepo;
import openerp.openerpresourceserver.service.OrderHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderHistoryRepo orderHistoryRepo;

    @Override
    public List<OrderHistoryResponseDto> getOrderHistory(UUID orderId) {
        List<OrderHistory> histories = orderHistoryRepo.findByOrderIdOrderByCreatedAtDesc(orderId);
        
        return histories.stream()
            .map(history -> OrderHistoryResponseDto.builder()
                .id(history.getId())
                .orderId(history.getOrderId())
                .status(history.getStatus())
                .version(history.getVersion())
                .changedBy(history.getChangedBy())
                .senderId(history.getSenderId())
                .senderName(history.getSenderName())
                .recipientId(history.getRecipientId())
                .recipientName(history.getRecipientName())
                .orderType(history.getOrderType())
                .totalPrice(history.getTotalPrice())
                .shippingPrice(history.getShippingPrice())
                .finalPrice(history.getFinalPrice())
                .origin(history.getOrigin())
                .destinationAddress(history.getDestinationAddress())
                .expectedDeliveryDate(history.getExpectedDeliveryDate())
                .originHubId(history.getOriginHubId())
                .originHubName(history.getOriginHubName())
                .finalHubId(history.getFinalHubId())
                .finalHubName(history.getFinalHubName())
                .changeReason(history.getChangeReason())
                .originalCreatedAt(history.getOriginalCreatedAt())
                .originalCreatedBy(history.getOriginalCreatedBy())
                .createdAt(history.getCreatedAt())
                .build())
            .collect(Collectors.toList());
    }
} 