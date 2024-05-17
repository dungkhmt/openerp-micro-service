package com.hust.wmsbackend.v2.controller;

import com.hust.wmsbackend.management.model.request.CartItemRequest;
import com.hust.wmsbackend.v2.model.request.SaleOrderRequest2;
import com.hust.wmsbackend.v2.model.response.CartItemResponse2;
import com.hust.wmsbackend.v2.service.CartService;
import com.hust.wmsbackend.v2.service.SaleOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/wmsv3/customer/cart")
@Validated
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CartController2 {

    private CartService cartService;
    private SaleOrderService saleOrderService;

    @PostMapping()
    public ResponseEntity<CartItemResponse2> calculateCartFee(@RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.calculateCartFee(request));
    }

    @PostMapping(path = "/create-order")
    public ResponseEntity<String> createSaleOrder(@RequestBody SaleOrderRequest2 request, Principal principal) {
        return saleOrderService.createSaleOrder(principal, request) ?
            ResponseEntity.ok("OK") :
            new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
