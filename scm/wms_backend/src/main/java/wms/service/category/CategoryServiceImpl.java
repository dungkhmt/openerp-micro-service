package wms.service.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.category.*;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.*;
import wms.service.BaseService;
import wms.utils.GeneralUtils;


@Service
public class CategoryServiceImpl extends BaseService implements ICategoryService {
    @Autowired
    private ProductUnitRepo productUnitRepo;
    @Autowired
    private ProductCategoryRepo productCategoryRepo;

    @Autowired
    private CustomerTypeRepo customerTypeRepo;

    @Autowired
    private DistributingChannelRepo distributingChannelRepo;

    @Autowired
    private ContractTypeRepo contractTypeRepo;
    @Override
    public ProductUnit createProductUnit(ProductUnitDTO productUnit) throws CustomException {
        ProductUnit newUnit = new ProductUnit();
        newUnit.setCode("UNIT" + GeneralUtils.generateCodeFromSysTime());
        newUnit.setName(productUnit.getName());
        return productUnitRepo.save(newUnit);
    }

    @Override
    public ReturnPaginationDTO<ProductUnit> getAllProductUnit(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<ProductUnit> listUnits = productUnitRepo.search(pageable);
        return getPaginationResult(listUnits.getContent(), page, listUnits.getTotalPages(), listUnits.getTotalElements());
    }

    @Override
    public ProductUnit getProductUnitById(long id) {
        return productUnitRepo.getProductUnitById(id);
    }

    @Override
    public ProductUnit getProductUnitByCode(String code) {
        return productUnitRepo.getProductUnitByCode(code.toUpperCase());
    }

    @Override
    public ProductUnit updateProductUnit(ProductUnitDTO productUnitDTO, long id) throws CustomException {
        ProductUnit updatingUnit = productUnitRepo.getProductUnitById(id);
        updatingUnit.setName(productUnitDTO.getName());
        return productUnitRepo.save(updatingUnit);
    }

    @Override
    public void deleteProductUnitByCode(String code) throws CustomException {
        ProductUnit productUnit = getProductUnitByCode(code);
        if (productUnit == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product unit not exists, can't delete");
        }
        productUnit.setDeleted(1);
        productUnitRepo.save(productUnit);
    }

    @Override
    public void deleteProductUnitById(long id) {
        productUnitRepo.deleteById(id);
    }

    /**
     * Product Category
     */
    @Override
    public ProductCategory createProductCategory(ProductCategoryDTO category) throws CustomException {
        ProductCategory newCate = new ProductCategory();
        newCate.setCode("PCAT" + GeneralUtils.generateCodeFromSysTime());
        newCate.setName(category.getName());
        return productCategoryRepo.save(newCate);
    }

    @Override
    public ReturnPaginationDTO<ProductCategory> getAllProductCategory(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<ProductCategory> listCategories = productCategoryRepo.search(pageable);
        return getPaginationResult(listCategories.getContent(), page, listCategories.getTotalPages(), listCategories.getTotalElements());
    }

    @Override
    public ProductCategory getProductCategoryById(long id) {
        return productCategoryRepo.getProductCategoryById(id);
    }

    @Override
    public ProductCategory getProductCategoryByCode(String code) {
        return productCategoryRepo.getProductCategoryByCode(code.toUpperCase());
    }

    @Override
    public ProductCategory updateProductCategory(ProductCategoryDTO productCategoryDTO, long id) throws CustomException {
        ProductCategory categoryToUpdate = productCategoryRepo.getProductCategoryById(id);
        categoryToUpdate.setName(productCategoryDTO.getName());
        return productCategoryRepo.save(categoryToUpdate);
    }

    @Override
    public void deleteProductCategoryByCode(String code) throws CustomException {
        ProductCategory productCategory = getProductCategoryByCode(code);
        if (productCategory == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product category not exists, can't delete");
        }
        productCategory.setDeleted(1);
        productCategoryRepo.save(productCategory);
    }

    @Override
    public void deleteProductCategoryById(long id) {
        productCategoryRepo.deleteById(id);
    }

    /**
     * Customer Type
     */
    @Override
    public CustomerType createCustomerType(CustomerTypeDTO customerTypeDTO) throws CustomException {
        CustomerType newType = new CustomerType();
        newType.setCode("CTYPE" + GeneralUtils.generateCodeFromSysTime());
        newType.setName(customerTypeDTO.getName());
        return customerTypeRepo.save(newType);
    }

    @Override
    public ReturnPaginationDTO<CustomerType> getAllCustomerType(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<CustomerType> listCustomerTypes = customerTypeRepo.search(pageable);
        return getPaginationResult(listCustomerTypes.getContent(), page, listCustomerTypes.getTotalPages(), listCustomerTypes.getTotalElements());
    }

    @Override
    public CustomerType getCustomerTypeById(long id) {
        return customerTypeRepo.getCustomerTypeById(id);
    }

    @Override
    public CustomerType getCustomerTypeByCode(String code) {
        return customerTypeRepo.getCustomerTypeByCode(code.toUpperCase());
    }

    @Override
    public CustomerType updateCustomerType(CustomerTypeDTO productCategoryDTO, long id) throws CustomException {
        CustomerType typeToUpdate = customerTypeRepo.getCustomerTypeById(id);
        typeToUpdate.setName(productCategoryDTO.getName());
        return customerTypeRepo.save(typeToUpdate);
    }

    @Override
    public void deleteCustomerTypeByCode(String code) throws CustomException {
        CustomerType customerType = getCustomerTypeByCode(code);
        if (customerType == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Customer type does not exists, can't delete");
        }
        customerType.setDeleted(1);
        customerTypeRepo.save(customerType);
    }

    @Override
    public void deleteCustomerTypeById(long id) {
        customerTypeRepo.deleteById(id);
    }

    /**
     * Distributing Channel
     * @param channelDTO
     * @return
     * @throws CustomException
     */
    @Override
    public DistributingChannel createDistributingChannel(DistributingChannelDTO channelDTO) throws CustomException {
        DistributingChannel newChannel = new DistributingChannel();
        newChannel.setCode("CHN" + GeneralUtils.generateCodeFromSysTime());
        newChannel.setName(channelDTO.getName());
        newChannel.setPromotion(channelDTO.getPromotion());
        return distributingChannelRepo.save(newChannel);
    }

    @Override
    public ReturnPaginationDTO<DistributingChannel> getAllDistributingChannel(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<DistributingChannel> listChannels = distributingChannelRepo.search(pageable);
        return getPaginationResult(listChannels.getContent(), page, listChannels.getTotalPages(), listChannels.getTotalElements());
    }

    @Override
    public DistributingChannel getDistributingChannelById(long id) {
        return distributingChannelRepo.getDistributingChannelById(id);
    }

    @Override
    public DistributingChannel getDistributingChannelByCode(String code) {
        return distributingChannelRepo.getDistributingChannelByCode(code.toUpperCase());
    }

    @Override
    public DistributingChannel updateDistributingChannel(DistributingChannelDTO channelDTO, long id) throws CustomException {
        DistributingChannel channelToUpdate = distributingChannelRepo.getDistributingChannelById(id);
        channelToUpdate.setName(channelDTO.getName());
        channelToUpdate.setPromotion(channelDTO.getPromotion());
        return distributingChannelRepo.save(channelToUpdate);
    }

    @Override
    public void deleteDistributingChannelByCode(String code) throws CustomException {
        DistributingChannel distributingChannel = getDistributingChannelByCode(code);
        if (distributingChannel == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Distribution channel not exists, can't delete");
        }
        distributingChannel.setDeleted(1);
        distributingChannelRepo.save(distributingChannel);
    }

    @Override
    public void deleteDistributingChannelById(long id) {
        distributingChannelRepo.deleteById(id);
    }

    /**
     * Contract Type
     * @param contractTypeDTO
     * @return
     * @throws CustomException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContractType createContractType(ContractTypeDTO contractTypeDTO) throws CustomException {
        ContractType newContract = new ContractType();
        DistributingChannel channel = distributingChannelRepo.getDistributingChannelByCode(contractTypeDTO.getChannelCode());
        if (channel == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Contract type with no specific distributing channel, can't create");
        }
        newContract.setCode("CTR" + GeneralUtils.generateCodeFromSysTime());
        newContract.setChannel(channel);
        newContract.setName(StringUtils.capitalize(contractTypeDTO.getName()));
        return contractTypeRepo.save(newContract);
    }

    @Override
    public ReturnPaginationDTO<ContractType> getAllContractType(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<ContractType> listContractTypes = contractTypeRepo.search(pageable);
        return getPaginationResult(listContractTypes.getContent(), page, listContractTypes.getTotalPages(), listContractTypes.getTotalElements());
    }

    @Override
    public ContractType getContractTypeById(long id) {
        return contractTypeRepo.getContractTypeById(id);
    }

    @Override
    public ContractType getContractTypeByCode(String code) {
        return contractTypeRepo.getContractTypeByCode(code);
    }

    @Override
    public ContractType updateContractType(ContractTypeDTO contractTypeDTO, long id) throws CustomException {
        ContractType typeToUpdate = contractTypeRepo.getContractTypeById(id);
        DistributingChannel channel = distributingChannelRepo.getDistributingChannelByCode(contractTypeDTO.getChannelCode());
        if (channel == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Contract type with no specific distributing channel, can't create");
        }
        typeToUpdate.setChannel(channel);
        typeToUpdate.setName(contractTypeDTO.getName());
        return contractTypeRepo.save(typeToUpdate);
    }

    @Override
    public void deleteContractTypeByCode(String code) throws CustomException {
        ContractType currContract = getContractTypeByCode(code);
        if (currContract == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Contract type not exists, can't delete");
        }
        currContract.setDeleted(1);
        contractTypeRepo.save(currContract);
    }

    @Override
    public void deleteContractTypeById(long id) throws CustomException {
        ContractType currContract = getContractTypeById(id);
        if (currContract == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Contract type not exists, can't delete");
        }
        currContract.setDeleted(1);
        contractTypeRepo.save(currContract);
    }

}
