package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.CustomerDTO;
import openerp.openerpresourceserver.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/customer")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CustomerController {

    private CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(Principal principal, CustomerDTO customerDTO) {

        customerService.registerCustomer(principal.getName(), customerDTO);
        return ResponseEntity.ok().body("Customer registered successfully");

    }
    @PostMapping
    public ResponseEntity<?> isCustomer(Principal principal) {

        boolean isTrue =  customerService.isCustomerExist(principal.getName());

        return ResponseEntity.ok().body(isTrue);
    }


}
