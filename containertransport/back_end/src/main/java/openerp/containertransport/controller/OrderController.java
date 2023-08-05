package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.MetaData;
import openerp.containertransport.dto.OrderFilterRequestDTO;
import openerp.containertransport.dto.OrderModel;
import openerp.containertransport.dto.OrderUpdateDTO;
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
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderModel orderModel, JwtAuthenticationToken token) {
        String username = token.getName();
        orderModel.setUsername(username);
        List<OrderModel> orders = orderService.createOrder(orderModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orders));
    }

    @PostMapping("/")
    public ResponseEntity<?> filterOrder(@RequestBody OrderFilterRequestDTO orderFilterRequestDTO, JwtAuthenticationToken token){
        String username = token.getName();
        List<String> roleIds = token
                .getAuthorities()
                .stream()
                .filter(grantedAuthority -> !grantedAuthority
                        .getAuthority()
                        .startsWith("ROLE_GR")) // remove all composite roles
                .map(grantedAuthority -> { // convert role to permission
                    String roleId = grantedAuthority.getAuthority().substring(5); // remove prefix "ROLE_"
                    return roleId;
                })
                .collect(Collectors.toList());
        if(roleIds.contains("TMS_CUSTOMER")) {
            orderFilterRequestDTO.setOwner(username);
        }
        OrdersRes orderModels = orderService.filterOrders(orderFilterRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orderModels));
    }

    @GetMapping("/{uid}")
    public ResponseEntity<?> getOrderByUid(@PathVariable String uid, JwtAuthenticationToken token) {
        String username = token.getName();
        boolean isCustomer = false;
        List<String> roleIds = token
                .getAuthorities()
                .stream()
                .filter(grantedAuthority -> !grantedAuthority
                        .getAuthority()
                        .startsWith("ROLE_GR")) // remove all composite roles
                .map(grantedAuthority -> { // convert role to permission
                    String roleId = grantedAuthority.getAuthority().substring(5); // remove prefix "ROLE_"
                    return roleId;
                })
                .collect(Collectors.toList());
        if(roleIds.contains("TMS_CUSTOMER")) {
            isCustomer = true;
        }
        OrderModel orderModel = orderService.getOrderByUid(uid, username, isCustomer);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orderModel));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable long id, @RequestBody OrderModel orderModel) {
        OrderModel orderModelUpdate = orderService.updateOrder(id, orderModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orderModelUpdate));
    }

    @PutMapping("/updateV2/{orderUid}")
    public ResponseEntity<?> updateOrderByCode(@PathVariable String orderUid, @RequestBody OrderModel orderModel) {
        OrderModel orderModelUpdate = orderService.updateOrderByUid(orderUid, orderModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orderModelUpdate));
    }

    @PutMapping("/update/")
    public ResponseEntity<?> updateOrderByCode(@RequestBody OrderUpdateDTO orderUpdateDTO) {
        List<OrderModel> orderModelsUpdate = orderService.updateListOrder(orderUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orderModelsUpdate));
    }

    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<?> deleteTruck(@PathVariable String uid) {
        OrderModel orderModel = orderService.deleteOrder(uid);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), orderModel));
    }
}
