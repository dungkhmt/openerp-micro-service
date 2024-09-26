package wms.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;
import wms.dto.ReturnPaginationDTO;
import wms.dto.product.ProductDTO;
import wms.dto.product.ProductDiscountDTO;
import wms.dto.product.ProductPriceDTO;
import wms.entity.ProductEntity;
import wms.entity.ProductPrice;
import wms.entity.ProductSalePrice;
import wms.exception.CustomException;

import java.io.IOException;
import java.util.List;


public interface IProductService {
    List<ProductEntity> createProductFromExcel(MultipartFile file) throws IOException, CustomException;
    ProductEntity createProduct(ProductDTO productDTO) throws CustomException;
    ReturnPaginationDTO<ProductEntity> getAllProducts(int page, int pageSize, String sortField, boolean isSortAsc,
    String productName, String status, String category, String unit, String textSearch
    ) throws JsonProcessingException;
    List<ProductEntity> getAllWithoutPaging();
    ProductEntity getProductById(long id);
    ProductEntity getProductByCode(String code);
    ProductEntity getProductBySku(String sku);
    ProductEntity updateProduct(ProductDTO productDTO, long id) throws CustomException;
    void deleteProductById(long id);
    void setPurchasePrice(List<ProductPriceDTO> productPriceDTO) throws CustomException;
    List<ProductPrice> getAllSellinPrice();
    ProductPrice updateSellinPrice(ProductPriceDTO productPriceDTO) throws CustomException;
    void deleteSellinPrice(Long id);
    void setSalePrice(List<ProductDiscountDTO> productDiscountDTO) throws CustomException;
    List<ProductSalePrice> getAllSelloutPrice();
    ProductSalePrice updateSelloutPrice(ProductDiscountDTO productDiscountDTO) throws CustomException;
    void deleteSelloutPrice(Long id);
}
