package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.OrderDTO;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@AllArgsConstructor(onConstructor_ = @Autowired)

public class OrderController {

    OrderService orderService;




    @PostMapping("/create")
    public ResponseEntity<?> createNewOrder(Principal principal, @RequestBody OrderDTO orderDTO) {
        try {
            return ResponseEntity.ok(orderService.createOrder(orderDTO, principal.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(Principal principal, @PathVariable UUID id, @RequestBody OrderDTO orderDTO) {
        try {
            return ResponseEntity.ok(orderService.updateOrder(id, orderDTO, principal.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(orderService.deleteOrder(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getOrder(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(orderService.getOrder(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllOrders() {
        try {
            return ResponseEntity.ok(orderService.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @GetMapping("/get-all-by-customer")
    public ResponseEntity<?> getByCustomer(Principal principal) {
        try {
            return ResponseEntity.ok(orderService.findAllByCustomerId(principal.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    //    @GetMapping("/get-all-by-customer")
    public ResponseEntity<?> getAllOrdersByCustomer(Principal principal) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // Lấy tên người dùng từ UserDetails
                String username = ((UserDetails) authentication.getPrincipal()).getUsername();

                // Lấy danh sách các vai trò của người dùng từ Authentication
                List<String> roles = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

                // Thực hiện các hành động dựa trên vai trò của người dùng
                if (roles.contains("ROLE_ADMIN")) {
                    // Xử lý cho vai trò Admin
                } else if (roles.contains("ROLE_USER")) {
                    // Xử lý cho vai trò User
                }

                // Tiếp tục với mã của bạn...

                return ResponseEntity.ok(orderService.findAllByCustomerId(username));
            } else {
                // Người dùng chưa được xác thực
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
