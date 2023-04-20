package wms.service.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import wms.dto.ReturnPaginationDTO;
import wms.dto.customer.CustomerDTO;
import wms.dto.customer.CustomerUpdateDTO;
import wms.dto.product.ProductDTO;
import wms.entity.Customer;
import wms.exception.CustomException;

public interface ICustomerService {
    Customer createNewCustomer(CustomerDTO customer) throws CustomException;
    ReturnPaginationDTO<Customer> getAllCustomers(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    Customer getCustomerById(long id);
    Customer getCustomerByCode(String code);
    Customer updateCustomerInfo(CustomerUpdateDTO customerDTO, long id) throws CustomException;
    void deleteCustomerById(long id);
}
