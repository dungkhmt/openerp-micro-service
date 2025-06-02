package openerp.openerpresourceserver.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.OrderHistoryResponseDto;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.OrderHistory;
import openerp.openerpresourceserver.repository.OrderHistoryRepo;
import openerp.openerpresourceserver.repository.OrderRepo;
import openerp.openerpresourceserver.service.OrderHistoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderHistoryRepo orderHistoryRepo;
    private final OrderRepo orderRepo;
    @Override
    public List<OrderHistoryResponseDto> getOrderHistory(UUID orderId) {
        List<OrderHistory> histories = orderHistoryRepo.findByOrderIdOrderByCreatedAtDesc(orderId);

        // Get current order to add current status
        Order currentOrder = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        List<OrderHistoryResponseDto> result = new ArrayList<>();

        // Add current status as the first entry (most recent)
        OrderHistoryResponseDto currentStatusDto = OrderHistoryResponseDto.builder()
                .id(UUID.randomUUID()) // Generate a temporary ID for current status
                .orderId(currentOrder.getId())
                .status(currentOrder.getStatus()) // Current status from Order table
                .version(currentOrder.getVersion())
                .changedBy(currentOrder.getChangedBy())
                .senderId(currentOrder.getSenderId())
                .senderName(currentOrder.getSenderName())
                .recipientId(currentOrder.getRecipientId())
                .recipientName(currentOrder.getRecipientName())
                .orderType(currentOrder.getOrderType())
                .totalPrice(currentOrder.getTotalPrice())
                .shippingPrice(currentOrder.getShippingPrice())
                .finalPrice(currentOrder.getFinalPrice())
                .origin(currentOrder.getOrigin())
                .destinationAddress(currentOrder.getDestinationAddress())
                .expectedDeliveryDate(currentOrder.getExpectedDeliveryDate())
                .originHubId(currentOrder.getOriginHubId())
                .originHubName(currentOrder.getOriginHubName())
                .finalHubId(currentOrder.getFinalHubId())
                .finalHubName(currentOrder.getFinalHubName())
                .changeReason("Current Status") // Indicate this is current status
                .originalCreatedAt(currentOrder.getCreatedAt())
                .originalCreatedBy(currentOrder.getCreatedBy())
                .createdAt(currentOrder.getUpdatedAt()) // Use updatedAt as the timestamp
                .build();

        result.add(currentStatusDto);
        // Add historical records
        List<OrderHistoryResponseDto> historicalRecords = histories.stream()
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

        result.addAll(historicalRecords);
        return result;
    }
} 