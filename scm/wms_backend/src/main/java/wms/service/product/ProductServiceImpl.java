package wms.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wms.common.enums.ErrorCode;
import wms.dto.ReturnPaginationDTO;
import wms.dto.product.ProductDTO;
import wms.dto.product.ProductDiscountDTO;
import wms.dto.product.ProductPriceDTO;
import wms.entity.*;
import wms.exception.CustomException;
import wms.repo.*;
import wms.service.BaseService;
import wms.utils.GeneralUtils;


@Service
@Slf4j
public class ProductServiceImpl extends BaseService implements IProductService {
    @Autowired
    private ProductSalePriceRepo productSalePriceRepo;
    @Autowired
    private ProductPriceRepo productPriceRepo;
    @Autowired
    private ContractTypeRepo contractTypeRepo;
    @Autowired
    private ProductRepo productRepository;
    @Autowired
    private ProductUnitRepo productUnitRepo;
    @Autowired
    private ProductCategoryRepo productCategoryRepo;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductEntity createProduct(ProductDTO productDTO) throws CustomException {
        if (productRepository.getProductBySku(productDTO.getSku()) != null) {
            throw caughtException(ErrorCode.ALREADY_EXIST.getCode(), "Exist product with same sku, can't create");
        }
        ProductUnit unit = productUnitRepo.getProductUnitById(productDTO.getUnitId());
        ProductCategory category = productCategoryRepo.getProductCategoryById(productDTO.getCategoryId());

        if (unit== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product with no specific unit, can't create");
        }
        if (category == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product with no specific category, can't create");
        }
        ProductEntity newProduct = ProductEntity.builder()
                .code("PRO" + GeneralUtils.generateCodeFromSysTime())
                .name(productDTO.getName())
                .unitPerBox(productDTO.getUnitPerBox())
                .productUnit(unit)
                .brand(productDTO.getBrand())
                .productCategory(category)
                .status(productDTO.getStatus())
                .massQuantity(productDTO.getMassQuantity())
                .sku(productDTO.getSku().toUpperCase())
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public ReturnPaginationDTO<ProductEntity> getAllProducts(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<ProductEntity> productList = productRepository.search(pageable);
        return getPaginationResult(productList.getContent(), page, productList.getTotalPages(), productList.getTotalElements());
    }

    @Override
    public ProductEntity getProductById(long id) {
        return productRepository.getProductById(id);
    }

    @Override
    public ProductEntity getProductByCode(String code) {
        return productRepository.getProductByCode(code.toUpperCase());
    }

    @Override
    public ProductEntity getProductBySku(String sku) {
        return productRepository.getProductByCode(sku.toUpperCase());
    }

    @Override
    public ProductEntity updateProduct(ProductDTO productDTO, long id) throws CustomException {
        ProductEntity productBySku = productRepository.getProductByCode(productDTO.getSku());
        if (productBySku != null && productBySku.getId() != id) {
            throw caughtException(ErrorCode.ALREADY_EXIST.getCode(), "Exist product with same sku, can't update");
        }
        ProductUnit unit = productUnitRepo.getProductUnitById(productDTO.getUnitId());
        ProductCategory category = productCategoryRepo.getProductCategoryById(productDTO.getCategoryId());

        if (unit== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product with no specific unit, can't update");
        }
        if (category == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product with no specific category, can't update");
        }
        ProductEntity productToUpdate = productRepository.getProductById(id);
        productToUpdate.setName(productDTO.getName());
        productToUpdate.setUnitPerBox(productDTO.getUnitPerBox());
        productToUpdate.setProductCategory(category);
        productToUpdate.setProductUnit(unit);
        productToUpdate.setBrand(productDTO.getBrand());
        productToUpdate.setStatus(productDTO.getStatus());
        productToUpdate.setMassQuantity(productDTO.getMassQuantity());
        productToUpdate.setSku(productDTO.getSku());
        return productRepository.save(productToUpdate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductById(long id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductPrice setPurchasePrice(ProductPriceDTO productPriceDTO) throws CustomException {
        ContractType contractType = contractTypeRepo.getContractTypeByCode(productPriceDTO.getContractTypeCode());
        ProductEntity product = productRepository.getProductByCode(productPriceDTO.getProductCode());
        if (contractType== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product price with no specific contract type, can't set");
        }
        if (product== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product price with no specific, can't set");
        }
        ProductPrice newPrice = ProductPrice.builder()
                .vat(productPriceDTO.getVat())
                .priceBeforeVat(productPriceDTO.getPriceBeforeVat())
                .status(productPriceDTO.getStatus())
                .startedDate(productPriceDTO.getStartedDate())
                .endedDate(productPriceDTO.getEndedDate())
                .contractType(contractType)
                .productEntity(product)
                .build();
        return productPriceRepo.save(newPrice) ;
    }

    @Override
    public ProductSalePrice setSalePrice(ProductDiscountDTO productDiscountDTO) throws CustomException {
        ContractType contractType = contractTypeRepo.getContractTypeByCode(productDiscountDTO.getContractTypeCode());
        ProductEntity product = productRepository.getProductByCode(productDiscountDTO.getProductCode());
        if (contractType== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product price with no specific contract type, can't set");
        }
        if (product== null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product price with no specific, can't set");
        }
        ProductSalePrice newPrice = ProductSalePrice.builder()
                .contractDiscount(productDiscountDTO.getContractDiscount())
                .massDiscount(productDiscountDTO.getMassDiscount())
                .contractType(contractType)
                .productEntity(product)
                .build();
        return productSalePriceRepo.save(newPrice) ;
    }
}
