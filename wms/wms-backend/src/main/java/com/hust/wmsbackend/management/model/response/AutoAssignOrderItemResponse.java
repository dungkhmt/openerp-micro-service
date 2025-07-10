package com.hust.wmsbackend.management.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AutoAssignOrderItemResponse {
    List<OrderDetailResponse.ProcessedOrderItemResponse> processingItems;
    List<OrderDetailResponse.OrderItemResponse> remainingItems;
}
