package wms.service.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.customer.CustomerDTO;
import wms.dto.product.ProductDTO;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.ContractTypeRepo;
import wms.repo.CustomerRepo;
import wms.repo.CustomerTypeRepo;
import wms.repo.UserRepo;
import wms.service.BaseService;

@Service
@Slf4j
public class CustomerServiceImpl extends BaseService implements ICustomerService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ContractTypeRepo contractTypeRepo;
    @Autowired
    private CustomerTypeRepo customerTypeRepo;
    @Autowired
    private CustomerRepo customerRepo;
    @Override
    public Customer createNewCustomer(CustomerDTO customer) throws CustomException {
        if (customerRepo.getCustomerByCode(customer.getCode()) != null) {
            throw caughtException(ErrorCode.ALREADY_EXIST.getCode(), "Exist customer with same code, can't create");
        }
        CustomerType customerType = customerTypeRepo.getCustomerTypeById(customer.getCustomerTypeId());
        ContractType contractType = contractTypeRepo.getContractTypeByCode(customer.getContractTypeCode());
        UserLogin user = userRepo.getUserByUserLoginId(customer.getCreatedBy());
        if (customerType== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Customer with no specific type, can't create");
        }
        if (contractType == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Customer with no specific contract, can't create");
        }
        if (user == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this customer, can't create");
        }
        Customer newCustomer = Customer.builder()
                .code(customer.getCode().toUpperCase())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .name(customer.getName())
                .longitude(customer.getLongitude())
                .latitude(customer.getLatitude())
                .status(customer.getStatus())
                .address(customer.getAddress())
                .customerType(customerType)
                .contractType(contractType)
                .user(user)
                .facilityCode(customer.getFacilityCode())
                .build();
        return customerRepo.save(newCustomer);
    }

    @Override
    public ReturnPaginationDTO<Customer> getAllCustomers(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        return null;
    }

    @Override
    public Customer getCustomerById(long id) {
        return null;
    }

    @Override
    public Customer getCustomerByCode(String code) {
        return null;
    }

    @Override
    public Customer updateCustomerInfo(CustomerDTO customerDTO, long id) throws CustomException {
        return null;
    }

    @Override
    public void deleteCustomerById(long id) {

    }
}
