package wms.service.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import wms.algorithms.utils.Utils;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.customer.CustomerDTO;
import wms.dto.customer.CustomerUpdateDTO;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.*;
import wms.service.BaseService;
import wms.service.files.IFileService;
import wms.utils.GeneralUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

    @Autowired
    private IFileService fileService;
    @Override
    @Transactional
    public void createCustomerFromFile(MultipartFile file, JwtAuthenticationToken token) throws IOException, CustomException {
        UserRegister createdBy = userRepo.getUserByUserLoginId(token.getName());
        if (createdBy == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Unknown staff create this customer, can't create");
        }
        Iterator<Row> rowIterator = fileService.initWorkbookRow(file);
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() == 0 || isRowEmpty(row)) continue;
            saveCustomers(row, createdBy);
        }
    }
    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK)
                return false;
        }
        return true;
    }
    private void saveCustomers(Row row, UserRegister createdBy) {
        List<Customer> listCustomers = new ArrayList<>();
        String customerName = row.getCell(6).getStringCellValue().equals("HUB") ? "Bưu cục " + row.getCell(4).getStringCellValue().replace("HUB", "") :
                "Bưu cục " + row.getCell(4).getStringCellValue();
        CustomerType customerType =  customerTypeRepo.getCustomerTypeByCode(row.getCell(16).getStringCellValue());
        ContractType contractType = contractTypeRepo.getContractTypeByCode(row.getCell(17).getStringCellValue());
        List<Facility> facilities = facilityRepo.getAllFacility();
        int bestFacilityIndex = 0;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < facilities.size(); i++) {
            double cusLat = Double.parseDouble(String.valueOf(row.getCell(9).getNumericCellValue()));
            double cusLon = Double.parseDouble(String.valueOf(row.getCell(10).getNumericCellValue()));
            double facLat = Double.parseDouble(facilities.get(i).getLatitude());
            double facLon = Double.parseDouble(facilities.get(i).getLongitude());
            double cusFacDistance = Utils.calculateCoordinationDistance(cusLat, cusLon, facLat, facLon);
            if (cusFacDistance < bestDistance) {
                bestDistance = cusFacDistance;
                bestFacilityIndex = i;
            }
        }
        Customer customer = Customer.builder()
                .code("CUS" + GeneralUtils.generateCodeFromSysTime() + row.getCell(0).getNumericCellValue())
                .phone(row.getCell(12).getCellTypeEnum().equals(CellType.NUMERIC) ?
                        String.valueOf(row.getCell(12).getNumericCellValue())
                        : row.getCell(12).getStringCellValue())
                .address(row.getCell(11).getStringCellValue())
                .province(row.getCell(3).getStringCellValue())
                .name(customerName)
                .longitude(String.valueOf(row.getCell(10).getNumericCellValue()))
                .latitude(String.valueOf(row.getCell(9).getNumericCellValue()))
                .status("active")
                .address(row.getCell(11).getStringCellValue())
                .customerType(customerType)
                .contractType(contractType)
                .user(createdBy)
                .facility(facilities.get(bestFacilityIndex))
                .build();

        listCustomers.add(customer);
        log.info("Num of customers added {}", listCustomers.size());
        customerRepo.saveAll(listCustomers);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer createNewCustomer(CustomerDTO customer, JwtAuthenticationToken token) throws CustomException {
        CustomerType customerType = customerTypeRepo.getCustomerTypeByCode(customer.getCustomerTypeCode().toUpperCase());
        ContractType contractType = contractTypeRepo.getContractTypeByCode(customer.getContractTypeCode().toUpperCase());
        UserRegister createdBy = userRepo.getUserByUserLoginId(token.getName());
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
                .province(customer.getProvince().length() > 0 ? customer.getProvince().toUpperCase() : null)
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
    public ReturnPaginationDTO<Customer> getAllCustomers(int page, int pageSize, String sortField, boolean isSortAsc, String customerName, String status, String createdBy, String address, String textSearch) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<Customer> customers = customerRepo.search(pageable, customerName, status, createdBy, address, textSearch);
        return getPaginationResult(customers.getContent(), page, customers.getTotalPages(), customers.getTotalElements());
    }
    @Override
    public List<Customer> getAllWithoutPaging() {
        return customerRepo.getAllCustomers();
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
    @Transactional
    public Customer updateCustomerInfo(CustomerUpdateDTO customerDTO, long id) throws CustomException {
        CustomerType customerType = customerTypeRepo.getCustomerTypeByCode(customerDTO.getCustomerTypeCode());
        ContractType contractType = contractTypeRepo.getContractTypeByCode(customerDTO.getContractTypeCode());
        if (customerType== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Customer with no specific type, can't update");
        }
        if (contractType == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Customer with no specific contract, can't update");
        }
        Customer customerToUpdate = customerRepo.getCustomerById(id);
        customerToUpdate.setName(customerDTO.getName());
        customerToUpdate.setPhone(customerDTO.getPhone());
        customerToUpdate.setAddress(customerDTO.getAddress());
        customerToUpdate.setStatus(customerDTO.getStatus());
        customerToUpdate.setLatitude(customerDTO.getLatitude());
        customerToUpdate.setLongitude(customerDTO.getLongitude());
        customerToUpdate.setContractType(contractType);
        customerToUpdate.setCustomerType(customerType);
        // Don't update user_created
        return customerRepo.save(customerToUpdate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomerById(long id) {
        Customer currCustomer = customerRepo.getCustomerById(id);
        currCustomer.setDeleted(1);
        customerRepo.save(currCustomer);
    }
}
