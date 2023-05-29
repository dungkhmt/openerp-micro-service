package wms.service.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.multipart.MultipartFile;
import wms.dto.ReturnPaginationDTO;
import wms.dto.customer.CustomerDTO;
import wms.dto.customer.CustomerUpdateDTO;
import wms.dto.product.ProductDTO;
import wms.entity.Customer;
import wms.entity.Facility;
import wms.entity.ProductEntity;
import wms.exception.CustomException;

public interface ICustomerService {
    void createCustomerFromFile(MultipartFile file, JwtAuthenticationToken token) throws IOException, CustomException;
//    List<Customer> importCustomer();
    Customer createNewCustomer(CustomerDTO customer,  JwtAuthenticationToken token) throws CustomException;
    ReturnPaginationDTO<Customer> getAllCustomers(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    List<Customer> getAllWithoutPaging();
    Customer getCustomerById(long id);
    Customer getCustomerByCode(String code);
    Customer updateCustomerInfo(CustomerUpdateDTO customerDTO, long id) throws CustomException;
    void deleteCustomerById(long id);
}
