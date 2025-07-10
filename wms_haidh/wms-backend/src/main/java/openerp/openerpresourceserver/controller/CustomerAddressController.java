package openerp.openerpresourceserver.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.CustomerAddressCreateRequest;
import openerp.openerpresourceserver.entity.CustomerAddress;
import openerp.openerpresourceserver.service.CustomerAddressService;

@RestController
@RequestMapping("/customer-addresses")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CustomerAddressController {

    @Autowired
    private CustomerAddressService customerAddressService;

    @Secured("ROLE_WMS_ONLINE_CUSTOMER")
    @GetMapping
    public List<CustomerAddress> getCustomerAddresses(Principal principal) {
        return customerAddressService.getCustomerAddressesByUserLoginId(principal.getName());
    }
    
    @Secured("ROLE_WMS_DELIVERY_MANAGER")
    @PostMapping("/list")
    public ResponseEntity<List<CustomerAddress>> getCustomerAddressesByIds(@RequestBody List<UUID> ids) {
        List<CustomerAddress> addresses = customerAddressService.getCustomerAddressesByIds(ids);
        return ResponseEntity.ok(addresses);
    }
    
    @Secured("ROLE_WMS_ONLINE_CUSTOMER")
    @PostMapping
    public ResponseEntity<CustomerAddress> createCustomerAddress(@RequestBody CustomerAddressCreateRequest request, Principal principal) {
        CustomerAddress newCustomerAddress = customerAddressService.createCustomerAddress(request, principal.getName());
        return new ResponseEntity<>(newCustomerAddress, HttpStatus.CREATED);
    }
}

