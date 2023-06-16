package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.MetaData;
import openerp.containertransport.dto.OrderFilterRequestDTO;
import openerp.containertransport.dto.OrderModel;
import openerp.containertransport.dto.OrdersRes;
import openerp.containertransport.dto.metaData.MetaDTO;
import openerp.containertransport.dto.metaData.ResponseMetaData;
import openerp.containertransport.entity.Order;
import openerp.containertransport.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderModel orderModel) {
        List<OrderModel> orders = orderService.createOrder(orderModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orders));
    }

    @PostMapping("/")
    public ResponseEntity<?> filterOrder(@RequestBody OrderFilterRequestDTO orderFilterRequestDTO, JwtAuthenticationToken token){
        String username = token.getName();
        orderFilterRequestDTO.setOwner(username);
        OrdersRes orderModels = orderService.filterOrders(orderFilterRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orderModels));
    }

    @GetMapping("/{orderCode}")
    public ResponseEntity<?> getOrderByOrderCode(@PathVariable String orderCode, JwtAuthenticationToken token) {
        String username = token.getName();
        OrderModel orderModel = orderService.getOrderByOrderCode(orderCode, username);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orderModel));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable long id, @RequestBody OrderModel orderModel) {
        OrderModel orderModelUpdate = orderService.updateOrder(id, orderModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orderModelUpdate));
    }

    @PutMapping("/update/{orderCode}")
    public ResponseEntity<?> updateOrderByCode(@PathVariable String orderCode, @RequestBody OrderModel orderModel) {
        OrderModel orderModelUpdate = orderService.updateOrderByCode(orderCode, orderModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orderModelUpdate));
    }
}
