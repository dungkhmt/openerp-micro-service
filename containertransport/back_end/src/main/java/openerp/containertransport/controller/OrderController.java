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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderModel orderModel) {
        OrderModel order = orderService.createOrder(orderModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), order));
    }

    @PostMapping("/")
    public ResponseEntity<?> filterOrder(@RequestBody OrderFilterRequestDTO orderFilterRequestDTO){
        OrdersRes orderModels = orderService.filterOrders(orderFilterRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orderModels));
    }


}
