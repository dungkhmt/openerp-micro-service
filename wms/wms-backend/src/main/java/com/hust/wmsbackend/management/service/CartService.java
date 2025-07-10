package com.hust.wmsbackend.management.service;

import com.hust.wmsbackend.management.model.request.CartItemRequest;
import com.hust.wmsbackend.management.model.response.CartItemResponse;

public interface CartService {

    CartItemResponse calculateCartFee(CartItemRequest request);

}
