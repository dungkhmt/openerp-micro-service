package com.hust.wmsbackend.v2.controller;

import com.hust.wmsbackend.management.auto.AutoAssignOrderItem;
import com.hust.wmsbackend.management.model.request.AssignedOrderItemRequest;
import com.hust.wmsbackend.management.model.response.AutoAssignOrderItemResponse;
import com.hust.wmsbackend.management.model.response.OrderDetailResponse;
import com.hust.wmsbackend.management.model.response.OrderGeneralResponse;
import com.hust.wmsbackend.v2.model.request.AssignedOrderItemRequest2;
import com.hust.wmsbackend.v2.model.response.AssignedOrderItemResponse;
import com.hust.wmsbackend.v2.service.AssignedOrderItemService;
import com.hust.wmsbackend.v2.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/wmsv3/admin/order")
@Validated
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class OrderController2 {

    private OrderService orderService;

    private AssignedOrderItemService assignedOrderItemService;

    private AutoAssignOrderItem autoAssignOrderItem;

    @GetMapping()
    public ResponseEntity<List<OrderGeneralResponse>> getAllOrdersByStatuses(
        @RequestParam(name = "orderStatus", required = false) String[] orderStatus) {
        return ResponseEntity.ok(orderService.getAllOrdersByStatuses(orderStatus));
    }

    @GetMapping(path = "/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetailById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderDetailById(orderId));
    }

    @PutMapping(path = "/approve/{orderId}")
    public ResponseEntity<String> approveOrder(Principal principal, @PathVariable String orderId) {
        return orderService.approve(principal, orderId) ? ResponseEntity.ok("OK") :
            new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping(path = "/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(Principal principal, @PathVariable String orderId) {
        return orderService.cancel(principal, orderId) ? ResponseEntity.ok("OK") :
            new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping(path = "/assign-order-item")
    public ResponseEntity<AssignedOrderItemResponse> createAssignedOrderItems(@Valid @RequestBody AssignedOrderItemRequest2 request) {
        return ResponseEntity.ok(assignedOrderItemService.create(request));
    }

    @PutMapping(path = "/auto-assign/{orderId}")
    public ResponseEntity<AutoAssignOrderItemResponse> autoAssignOrderItems(@PathVariable String orderId) {
        AutoAssignOrderItemResponse response = autoAssignOrderItem.assign(orderId);
        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/for-warehouse-manager/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetailForWhManagerById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderDetailForWhManagerById(orderId));
    }
}
