package com.hust.wmsbackend.v2.service;

import com.hust.wmsbackend.management.model.request.CartItemRequest;
import com.hust.wmsbackend.v2.model.response.CartItemResponse2;

public interface CartService {

    CartItemResponse2 calculateCartFee(CartItemRequest request);

}
