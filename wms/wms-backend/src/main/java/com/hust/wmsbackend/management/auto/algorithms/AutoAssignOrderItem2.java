package com.hust.wmsbackend.management.auto.algorithms;

import com.hust.wmsbackend.management.model.response.AutoAssignOrderItemResponse;

public interface AutoAssignOrderItem2 {

    AutoAssignOrderItemResponse assign(String orderId);

}
