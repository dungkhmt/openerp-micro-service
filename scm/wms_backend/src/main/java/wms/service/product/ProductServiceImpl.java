package wms.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
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

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


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
    public List<ProductEntity> createProductFromExcel(MultipartFile file) throws IOException, CustomException {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (fileName.equals("")) {
            throw caughtException(ErrorCode.USER_ACTION_FAILED.getCode(), "Hãy tải file lên hệ thống!");
        }
        String fileExt = "";
        if (fileName.contains(".")) {
            fileExt = fileName.substring(fileName.lastIndexOf("."));
        }
        if (!fileExt.contains("xls")) {
            throw caughtException(ErrorCode.FORMAT.getCode(), "Wrong file type. Only support .xls and .xlsx file extensions");
        }

//        FileInputStream fis = new FileInputStream(path.replace(".", "data/file/") + fileName);
        InputStream fileStream = file.getInputStream();
        Workbook workbook = null;
        Sheet sheet = null;
        if (fileExt.equals(".xls")) {
            workbook = new HSSFWorkbook(fileStream);
            sheet = workbook.getSheetAt(0);
        }
        else {
            workbook = new XSSFWorkbook(fileStream);
            sheet = workbook.getSheetAt(0);
        }
        Iterator<Row> rowIterator = sheet.iterator();
        List<ProductEntity> listProduct = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() == 0 || isRowEmpty(row)) continue;
            ProductEntity productEntity = new ProductEntity();
            listProduct.add(productEntity);
        }
        log.info("Num of product added {}", listProduct.size());
        productRepository.saveAll(listProduct);
        return listProduct;
    }

    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK)
                return false;
        }
        return true;
    }

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
    public ReturnPaginationDTO<ProductEntity> getAllProducts(int page, int pageSize, String sortField, boolean isSortAsc, String productName, String status, String category, String unit, String textSearch) throws JsonProcessingException {
        Pageable pageable = StringHelper.isEmpty(sortField) ? getDefaultPage(page, pageSize)
                : isSortAsc ? PageRequest.of(page - 1, pageSize, Sort.by(sortField).ascending())
                : PageRequest.of(page - 1, pageSize, Sort.by(sortField).descending());
        Page<ProductEntity> productList = productRepository.search(pageable, productName, status, category, unit, textSearch);
        return getPaginationResult(productList.getContent(), page, productList.getTotalPages(), productList.getTotalElements());
    }

    @Override
    public List<ProductEntity> getAllWithoutPaging() {
        return productRepository.getAllProduct();
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
    @Transactional(rollbackFor = Exception.class)
    public void setPurchasePrice(List<ProductPriceDTO> productPriceDTO) throws CustomException {
        for (ProductPriceDTO priceDTO : productPriceDTO) {
//            ContractType contractType = contractTypeRepo.getContractTypeByCode(priceDTO.getContractTypeCode());
            ProductEntity product = productRepository.getProductByCode(priceDTO.getProductCode());
//            if (contractType== null) {
//                throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product price with no specific contract type, can't set");
//            }
            if (product== null) {
                throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product price with no specific, can't set");
            }
            ProductPrice newPrice = ProductPrice.builder()
                    .vat(priceDTO.getVat())
                    .priceBeforeVat(priceDTO.getPriceBeforeVat())
                    .status(priceDTO.getStatus())
                    .startedDate(priceDTO.getStartedDate())
                    .endedDate(priceDTO.getEndedDate())
//                    .contractType(contractType)
                    .productEntity(product)
                    .build();
            productPriceRepo.save(newPrice);
        }
    }

    @Override
    public List<ProductPrice> getAllSellinPrice() {
        return productPriceRepo.getAll();
    }

    @Override
    public ProductPrice updateSellinPrice(ProductPriceDTO productPriceDTO) throws CustomException {
        ProductPrice productPrice = productPriceRepo.getByProductCode(productPriceDTO.getProductCode());
        if (productPrice == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "None existed product, can't update");
        }
        productPrice.setPriceBeforeVat(productPriceDTO.getPriceBeforeVat());
        productPrice.setVat(productPriceDTO.getVat());
        productPrice.setStartedDate(productPriceDTO.getStartedDate());
        productPrice.setEndedDate(productPriceDTO.getEndedDate());
        return productPriceRepo.save(productPrice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSellinPrice(Long id) {
        productPriceRepo.deleteById(id);
    }

    @Override
    public void setSalePrice(List<ProductDiscountDTO> productDiscountDTO) throws CustomException {
        for (ProductDiscountDTO discountDTO : productDiscountDTO) {
            ContractType contractType = contractTypeRepo.getContractTypeByCode(discountDTO.getContractTypeCode());
            ProductEntity product = productRepository.getProductByCode(discountDTO.getProductCode());
            if (contractType == null) {
                throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product price with no specific contract type, can't set");
            }
            if (product == null) {
                throw caughtException(ErrorCode.NON_EXIST.getCode(), "Product price with no specific, can't set");
            }
//            double priceAfterAll = discountDTO.getPriceAfterVat() * (100 - discountDTO.getContractDiscount() - discountDTO.getMassDiscount()) / 100;
            ProductSalePrice newPrice = ProductSalePrice.builder()
                    .contractDiscount(discountDTO.getContractDiscount())
                    .massDiscount(discountDTO.getMassDiscount())
                    .contractType(contractType)
                    .productEntity(product)
//                    .priceAfterVat(discountDTO.getPriceAfterVat())
//                    .priceAfterAll(priceAfterAll)
                    .build();

            productSalePriceRepo.save(newPrice);
        }
    }
    @Override
    public List<ProductSalePrice> getAllSelloutPrice() {
        return productSalePriceRepo.getAll();
    }

    @Override
    public ProductSalePrice updateSelloutPrice(ProductDiscountDTO productDiscountDTO) throws CustomException {
        ProductSalePrice productSalePrice = productSalePriceRepo.getByProductAndContract(productDiscountDTO.getProductCode(), productDiscountDTO.getContractTypeCode());
        if (productSalePrice == null) {
            throw caughtException(ErrorCode.NON_EXIST.getCode(), "None existed product, can't update");
        }
        productSalePrice.setContractDiscount(productDiscountDTO.getContractDiscount());
        productSalePrice.setMassDiscount(productDiscountDTO.getMassDiscount());
//        double priceAfterAll = productDiscountDTO.getPriceAfterVat() * (100 - productDiscountDTO.getMassDiscount() - productDiscountDTO.getContractDiscount()) / 100;
//        productSalePrice.setPriceAfterAll(priceAfterAll);
        return productSalePriceRepo.save(productSalePrice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSelloutPrice(Long id) {
        productSalePriceRepo.deleteById(id);
    }
}
