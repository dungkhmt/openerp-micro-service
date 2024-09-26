package wms.service.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import wms.dto.ReturnPaginationDTO;
import wms.dto.category.*;
import wms.entity.*;
import wms.exception.CustomException;

import java.util.List;

public interface ICategoryService {
    // Product Unit
    ProductUnit createProductUnit(ProductUnitDTO productUnit) throws CustomException;
    ReturnPaginationDTO<ProductUnit> getAllProductUnit(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    ProductUnit getProductUnitById(long id);
    ProductUnit getProductUnitByCode(String code);
    ProductUnit updateProductUnit(ProductUnitDTO productUnitDTO, long id) throws CustomException;
    void deleteProductUnitByCode(String code) throws CustomException;
    void deleteProductUnitById(long id);
    // Product Category
    ProductCategory createProductCategory(ProductCategoryDTO category) throws CustomException;
    ReturnPaginationDTO<ProductCategory> getAllProductCategory(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    ProductCategory getProductCategoryById(long id);
    ProductCategory getProductCategoryByCode(String code);
    ProductCategory updateProductCategory(ProductCategoryDTO productCategoryDTO, long id) throws CustomException;
    void deleteProductCategoryByCode(String code) throws CustomException;
    void deleteProductCategoryById(long id);

    // Customer Type
    CustomerType createCustomerType(CustomerTypeDTO category) throws CustomException;
    ReturnPaginationDTO<CustomerType> getAllCustomerType(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    CustomerType getCustomerTypeById(long id);
    CustomerType getCustomerTypeByCode(String code);
    CustomerType updateCustomerType(CustomerTypeDTO productCategoryDTO, long id) throws CustomException;
    void deleteCustomerTypeByCode(String code) throws CustomException;
    void deleteCustomerTypeById(long id);

    // Distributing Channel
    DistributingChannel createDistributingChannel(DistributingChannelDTO channelDTO) throws CustomException;
    ReturnPaginationDTO<DistributingChannel> getAllDistributingChannel(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    DistributingChannel getDistributingChannelById(long id);
    DistributingChannel getDistributingChannelByCode(String code);
    DistributingChannel updateDistributingChannel(DistributingChannelDTO channelDTO, long id) throws CustomException;
    void deleteDistributingChannelByCode(String code) throws CustomException;
    void deleteDistributingChannelById(long id);

    // Contract Type
    ContractType createContractType(ContractTypeDTO contractTypeDTO) throws CustomException;
    ReturnPaginationDTO<ContractType> getAllContractType(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    ContractType getContractTypeById(long id);
    ContractType getContractTypeByCode(String code);
    ContractType updateContractType(ContractTypeDTO contractTypeDTO, long id) throws CustomException;
    void deleteContractTypeByCode(String code) throws CustomException;
    void deleteContractTypeById(long id) throws CustomException;
}
