package openerp.openerpresourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public List<CustomerAddress> getCustomerAddresses(@RequestParam String userLoginId) {
        return customerAddressService.getCustomerAddressesByUserLoginId(userLoginId);
    }
    
    @PostMapping
    public ResponseEntity<CustomerAddress> createCustomerAddress(@RequestBody CustomerAddressCreateRequest request) {
        CustomerAddress newCustomerAddress = customerAddressService.createCustomerAddress(request);
        return new ResponseEntity<>(newCustomerAddress, HttpStatus.CREATED);
    }
}

