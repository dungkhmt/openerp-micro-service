package com.hust.wmsbackend.management.service.service2;

import com.hust.wmsbackend.management.model.request.CartItemRequest;
import com.hust.wmsbackend.management.model.model2.response.CartItemResponse2;

public interface CartService {

    CartItemResponse2 calculateCartFee(CartItemRequest request);

}
