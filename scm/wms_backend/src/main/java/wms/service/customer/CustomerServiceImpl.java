package wms.service.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wms.algorithms.utils.Utils;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.customer.CustomerDTO;
import wms.dto.customer.CustomerUpdateDTO;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.*;
import wms.service.BaseService;
import wms.utils.GeneralUtils;

import java.util.List;

@Service
@Slf4j
public class CustomerServiceImpl extends BaseService implements ICustomerService {
    @Autowired
    private FacilityRepo facilityRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ContractTypeRepo contractTypeRepo;
    @Autowired
    private CustomerTypeRepo customerTypeRepo;
    @Autowired
    private CustomerRepo customerRepo;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer createNewCustomer(CustomerDTO customer, JwtAuthenticationToken token) throws CustomException {
        CustomerType customerType = customerTypeRepo.getCustomerTypeByCode(customer.getCustomerTypeCode().toUpperCase());
        ContractType contractType = contractTypeRepo.getContractTypeByCode(customer.getContractTypeCode().toUpperCase());
        UserLogin createdBy = userRepo.getUserByUserLoginId(token.getName());
//        Facility facility = facilityRepo.getFacilityByCode(customer.getFacilityCode().toUpperCase());
        if (customerType== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Customer with no specific type, can't create");
        }
        if (contractType == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Customer with no specific contract, can't create");
        }
        if (createdBy == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this customer, can't create");
        }
        if (customer.getLongitude() == "" || customer.getLatitude() == "") {
            throw caughtException(ErrorCode.FORMAT.getCode(), "Location didn't validate, can't create");
        }
//        if (facility == null) {
//            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Customer with no specific facility, can't create");
//        }
        List<Facility> facilities = facilityRepo.getAllFacility();
        int bestFacilityIndex = 0;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < facilities.size(); i++) {
            double cusLat = Double.parseDouble(customer.getLatitude());
            double cusLon = Double.parseDouble(customer.getLongitude());
            double facLat = Double.parseDouble(facilities.get(i).getLatitude());
            double facLon = Double.parseDouble(facilities.get(i).getLongitude());
            double cusFacDistance = Utils.calculateCoordinationDistance(cusLat, cusLon, facLat, facLon);
            if (cusFacDistance < bestDistance) {
                bestDistance = cusFacDistance;
                bestFacilityIndex = i;
            }
        }

        Customer newCustomer = Customer.builder()
                .code("CUS" + GeneralUtils.generateCodeFromSysTime())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .name(customer.getName())
                .longitude(customer.getLongitude())
                .latitude(customer.getLatitude())
                .status(customer.getStatus())
                .address(customer.getAddress())
                .customerType(customerType)
                .contractType(contractType)
                .user(createdBy)
                .facility(facilities.get(bestFacilityIndex))
                .build();
        return customerRepo.save(newCustomer);
    }

    @Override
    public ReturnPaginationDTO<Customer> getAllCustomers(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<Customer> customers = customerRepo.search(pageable);
        return getPaginationResult(customers.getContent(), page, customers.getTotalPages(), customers.getTotalElements());
    }

    @Override
    public Customer getCustomerById(long id) {
        return customerRepo.getCustomerById(id);
    }

    @Override
    public Customer getCustomerByCode(String code) {
        return customerRepo.getCustomerByCode(code.toUpperCase());
    }

    @Override
    public Customer updateCustomerInfo(CustomerUpdateDTO customerDTO, long id) throws CustomException {
        Customer customer = customerRepo.getCustomerByCode(customerDTO.getCode());
        if (customer != null && customer.getId() != id) {
            throw caughtException(ErrorCode.ALREADY_EXIST.getCode(), "Exist customer with same code, can't update");
        }
        CustomerType customerType = customerTypeRepo.getCustomerTypeById(customerDTO.getCustomerTypeId());
        ContractType contractType = contractTypeRepo.getContractTypeByCode(customerDTO.getCode().toUpperCase());
        if (customerType== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Customer with no specific type, can't update");
        }
        if (contractType == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Customer with no specific contract, can't update");
        }
        Customer customerToUpdate = customerRepo.getCustomerById(id);
        customerToUpdate.setCode(customerDTO.getCode());
        customerToUpdate.setName(customerDTO.getName());
        customerToUpdate.setPhone(customerDTO.getPhone());
        customerToUpdate.setAddress(customerDTO.getAddress());
        customerToUpdate.setStatus(customerDTO.getStatus());
        customerToUpdate.setLatitude(customerToUpdate.getLatitude());
        customerToUpdate.setLongitude(customerToUpdate.getLongitude());
        // Don't update user_created
        return customerRepo.save(customerToUpdate);
    }

    @Override
    public void deleteCustomerById(long id) {
        customerRepo.deleteById(id);
    }
}
