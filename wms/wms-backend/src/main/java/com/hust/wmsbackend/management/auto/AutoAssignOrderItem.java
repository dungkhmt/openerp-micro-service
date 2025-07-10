package com.hust.wmsbackend.management.auto;

import com.hust.wmsbackend.management.model.response.AutoAssignOrderItemResponse;

public interface AutoAssignOrderItem {

    AutoAssignOrderItemResponse assign(String orderId);

}
