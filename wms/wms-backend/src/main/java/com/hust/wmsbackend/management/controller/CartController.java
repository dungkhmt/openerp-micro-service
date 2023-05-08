package com.hust.wmsbackend.management.controller;

import com.hust.wmsbackend.management.model.request.CartItemRequest;
import com.hust.wmsbackend.management.model.request.SaleOrderRequest;
import com.hust.wmsbackend.management.model.response.CartItemResponse;
import com.hust.wmsbackend.management.service.CartService;
import com.hust.wmsbackend.management.service.SaleOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/wmsv2/customer/cart")
@CrossOrigin
@Validated
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CartController {

    private CartService cartService;
    private SaleOrderService saleOrderService;

    @PostMapping()
    public ResponseEntity<CartItemResponse> calculateCartFee(@RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.calculateCartFee(request));
    }

    @PostMapping(path = "/create-order")
    public ResponseEntity<String> createSaleOrder(@RequestBody SaleOrderRequest request, Principal principal) {
        return saleOrderService.createSaleOrder(principal, request) ?
            ResponseEntity.ok("OK") :
            new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
