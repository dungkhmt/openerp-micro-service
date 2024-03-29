package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.dto.CustomerDTO;
import openerp.openerpresourceserver.entity.Customer;
import openerp.openerpresourceserver.repo.CustomerRepo;
import openerp.openerpresourceserver.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService{

   @Autowired
   private CustomerRepo customerRepo;

    @Override
    public void registerCustomer(String userId, CustomerDTO customerDTO) {

        customerRepo.save(Customer.builder()
                .userId(userId)
                .customerName(customerDTO.getCustomerName())
                .address(customerDTO.getAddress())
                .latitude(customerDTO.getLatitude())
                .longitude(customerDTO.getLongitude())
                .phone(customerDTO.getPhone())
                .status(customerDTO.getStatus())
                .build());


    }

    @Override
    public boolean isCustomerExist(String userId) {
        return customerRepo.findFirstByUserId(userId).isPresent();
    }
}
