package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.CustomerDTO;
import openerp.openerpresourceserver.entity.Customer;

public interface CustomerService {
    void registerCustomer(String userId, CustomerDTO customerDTO);

    boolean isCustomerExist(String userId);
}
